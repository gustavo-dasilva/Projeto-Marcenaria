/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.maiormoveis.telas;

import br.com.maiormoveis.classes.Usuario;
import br.com.maiormoveis.classes.Cliente;
import br.com.maiormoveis.classes.Pedido;
import br.com.maiormoveis.classes.Pessoa;
import br.com.maiormoveis.dal.ModuloConexao;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author euraf
 */
public class TelaPedidos extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    //variável para armazenar o radio button
    private String tipo;

    /**
     * Creates new form TelaPedidos
     */
    public TelaPedidos() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void limparTelaPed() {
        txtIdVendedor.setEnabled(true);
        txtNpedido.setText(null);
        txtData.setText(null);
        txtProduto.setText(null);
        txaDesc.setText(null);
        txtCusto.setText(null);
        txtIdVendedor.setText(null);
        lblValorTotal.setText(null);
        txtPesquisarCliente.setText(null);
        txtIdCliente.setText(null);
        ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
        btnPedExc.setEnabled(false);
        btnPedAlt.setEnabled(false);
        btnPedImp.setEnabled(false);
        btnPedCons.setEnabled(true);
        btnPedAd.setEnabled(true);
        txtPesquisarCliente.setEnabled(true);
        tblClientes.setVisible(true);
        lblNomeVend.setText(null);
        lblStatusVend.setText(null);

    }

    private void pesquisar_cliente() {
        String sql = "select idcliente as ID, nomecliente as Nome, fonecliente as Telefone from tbclientes where nomecliente like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarCliente.getText() + "%");
            rs = pst.executeQuery();
            //Utiliza a biblioteca rs2xml.jar para preencher a tabela.
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void setar_campos() {
        int setar = tblClientes.getSelectedRow();
        txtIdCliente.setText(tblClientes.getModel().getValueAt(setar, 0).toString());
        //Criar nova consulta ao banco de dados para verificar o status do cliente e, caso estiver inativo abrir uma janela falando..
        //Dentro do try colocar um if else para verificar o status. Executar o código acima se status for ativo.
        String sql = "select status from tbclientes where idcliente=?";
        Usuario usuario = new Usuario();
        usuario.setId(tblClientes.getModel().getValueAt(setar, 0).toString());

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, usuario.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                String status = rs.getString(1);
                if (status.equals("Inativo")) {
                    JOptionPane.showMessageDialog(null, "Cliente Inativo. Escolha outro cliente.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    txtPesquisarCliente.setText(null);
                    txtIdCliente.setText(null);
                    ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
                    SwingUtilities.invokeLater(() -> txtPesquisarCliente.requestFocus());
                } else {

                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuário e/ou senha inválido(s)");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void cadastrar_pedido() {
        String sql = "insert into tbpedidos(produto, quant, descr, custo, idvendedor, markup, valor_orc, idcliente, status, tipo) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if ((txtProduto.getText().isEmpty()) || (txtCusto.getText().isEmpty()) || (txtIdVendedor.getText().isEmpty()) || (txtIdCliente.getText().isEmpty()) || (lblStatusVend.getText().isEmpty())) {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        } else {
            if (lblStatusVend.getText().equals("Inativo") || (lblStatusVend.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Selecione um vendedor ativo.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                lblStatusVend.setText(null);
                txtIdVendedor.setText(null);
                txtIdVendedor.setEnabled(true);
                lblNomeVend.setText(null);

            } else {

                Pedido pedido = new Pedido(); // Criar objeto pedido
                pedido.setProduto(txtProduto.getText());
                int quant = (Integer) spnQuantidade.getValue();
                pedido.setQuantidade(quant);
                pedido.setDescricao(txaDesc.getText());
                pedido.setCusto(Double.parseDouble(txtCusto.getText().replace(",", ".")));
                pedido.setIdvendedor(txtIdVendedor.getText());
                int markup = (Integer) spnMarkup.getValue();
                pedido.setMarkup(markup);
                pedido.setOrcamento((pedido.getCusto() * markup * quant));
                pedido.setIdcliente(txtIdCliente.getText());
                pedido.setStatus(cboStatus.getSelectedItem().toString());
                pedido.setTipo(tipo);
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setString(1, pedido.getProduto());
                    pst.setString(2, String.valueOf(pedido.getQuantidade()));
                    pst.setString(3, pedido.getDescricao());
                    pst.setString(4, String.valueOf(pedido.getCusto()));
                    pst.setString(5, pedido.getIdvendedor());
                    pst.setString(6, String.valueOf(pedido.getMarkup()));
                    pst.setString(7, String.valueOf(pedido.getOrcamento()));
                    pst.setString(8, pedido.getIdcliente());
                    pst.setString(9, pedido.getStatus());
                    pst.setString(10, pedido.getTipo());

                    int cadastrado = pst.executeUpdate();
                    if (cadastrado > 0) {
                        JOptionPane.showMessageDialog(null, "Pedido cadastrado com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                        String sql1 = "SELECT npedido FROM tbpedidos ORDER BY npedido DESC LIMIT 1";
                        try {
                            pst = conexao.prepareStatement(sql1);
                            rs = pst.executeQuery();
                            if (rs.next()) {
                                Pedido pedido1 = new Pedido();
                                pedido1.setNPedido(rs.getInt(1));
                                JOptionPane.showMessageDialog(null, "Número do Pedido: " + pedido1.getNPedido(), "Aviso", JOptionPane.INFORMATION_MESSAGE);

                            } else {
                                JOptionPane.showMessageDialog(null, "Erro ao exibir o número do pedido.");
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }
                        limparTelaPed();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }

        }
    }

    private void pesquisar_pedido() {
        String num_pedido = JOptionPane.showInputDialog("Numero do Pedido");
        String sql = "select * from tbpedidos where npedido= " + num_pedido;

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setNPedido(rs.getInt(1));
                Date dataPedido = rs.getDate(2);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataFormatada = sdf.format(dataPedido);
                pedido.setDataPedido(dataFormatada);
                pedido.setProduto(rs.getString(3));
                pedido.setQuantidade(rs.getInt(4));
                pedido.setDescricao(rs.getString(5));
                pedido.setCusto(rs.getDouble(6));
                pedido.setIdvendedor(rs.getString(7));
                pedido.setMarkup(rs.getInt(8));
                pedido.setOrcamento(rs.getDouble(9));
                pedido.setIdcliente(rs.getString(10));
                pedido.setStatus(rs.getString(11));
                pedido.setTipo(rs.getString(12));
                txtNpedido.setText(String.valueOf(pedido.getNPedido()));
                txtData.setText(pedido.getDataPedido());
                txtProduto.setText(pedido.getProduto());
                txaDesc.setText(pedido.getDescricao());
                txtCusto.setText(String.valueOf(pedido.getCusto()));
                txtIdVendedor.setText(pedido.getIdvendedor());
                lblValorTotal.setText("R$ " + String.valueOf(pedido.getOrcamento()));
                spnQuantidade.setValue(pedido.getQuantidade());
                spnMarkup.setValue(pedido.getMarkup());
                cboStatus.setSelectedItem(pedido.getStatus());
                txtIdCliente.setText(pedido.getIdcliente());
                if (pedido.getTipo().equals("Orçamento")) {
                    rbtOrc.setSelected(true);
                    tipo = "Orçamento";
                } else {
                    rbtPedido.setSelected(true);
                    tipo = "Pedido";
                }
                btnPedAd.setEnabled(false);
                txtPesquisarCliente.setEnabled(false);
                tblClientes.setVisible(false);
                txtIdVendedor.setEnabled(false);
                btnPedExc.setEnabled(true);
                btnPedAlt.setEnabled(true);
                btnPedImp.setEnabled(true);

            } else {
                JOptionPane.showMessageDialog(null, "Pedido não cadastrado!");
                limparTelaPed();
            }

        } catch (java.sql.SQLSyntaxErrorException e2) {
            JOptionPane.showMessageDialog(null, "Pedido inválido!", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void alterar_pedido() {
        String sql = "update tbpedidos set produto=?, quant=?, descr=?, custo=?, markup=?, valor_orc=?, status=?, tipo=? where npedido=?";

        if ((txtProduto.getText().isEmpty()) || (txtCusto.getText().isEmpty())) {
            JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

        } else {
            Pedido pedido = new Pedido();
            pedido.setProduto(txtProduto.getText());
            int quant = (Integer) spnQuantidade.getValue();
            pedido.setQuantidade(quant);
            pedido.setDescricao(txaDesc.getText());
            pedido.setCusto(Double.parseDouble(txtCusto.getText().replace(",", ".")));
            int markup = (Integer) spnMarkup.getValue();
            pedido.setMarkup(markup);
            pedido.setOrcamento((pedido.getCusto() * markup * quant));
            pedido.setStatus(cboStatus.getSelectedItem().toString());
            pedido.setTipo(tipo);
            pedido.setNPedido(Integer.parseInt(txtNpedido.getText()));
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, pedido.getProduto());
                pst.setString(2, String.valueOf(pedido.getQuantidade()));
                pst.setString(3, pedido.getDescricao());
                pst.setString(4, String.valueOf(pedido.getCusto()));
                pst.setString(5, String.valueOf(pedido.getMarkup()));
                pst.setString(6, String.valueOf(pedido.getOrcamento()));
                pst.setString(7, pedido.getStatus());
                pst.setString(8, pedido.getTipo());
                pst.setString(9, String.valueOf(pedido.getNPedido()));
                int atualizado = pst.executeUpdate();
                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do pedido alterados com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limparTelaPed();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void excluir_pedido() {
        int deletar = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este pedido?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (deletar == JOptionPane.YES_OPTION) {
            String sql = "delete from tbpedidos where npedido=?";
            Pedido pedido = new Pedido();
            pedido.setNPedido(Integer.parseInt(txtNpedido.getText()));
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, String.valueOf(pedido.getNPedido()));
                int deletado = pst.executeUpdate();
                if (deletado > 0) {
                    JOptionPane.showMessageDialog(null, "Pedido excluído com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limparTelaPed();
                    btnPedAd.setEnabled(true);
                    txtPesquisarCliente.setEnabled(true);
                    tblClientes.setVisible(true);

                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }
    }

    private void imp_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Confirma a emissão do relatório deste pedido?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            try {
                //usando hashmap para criar filtro do relatório.
                HashMap filtro = new HashMap();
                filtro.put("npedido", Integer.parseInt(txtNpedido.getText()));//npedido é a variável 'criada' no ireport. É preciso converter para int.
                JasperPrint print = JasperFillManager.fillReport("D://reports/pedido.jasper", filtro, conexao);//o segundo parâmetro é o hashmap.
                JasperViewer.viewReport(print, false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void consultar_vendedor() {
        String sql = "select * from tbusuarios where iduser=?";
        Usuario usuario = new Usuario();
        usuario.setId(txtIdVendedor.getText());
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, usuario.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                lblNomeVend.setText(rs.getString(2));
                lblStatusVend.setText(rs.getString(7));
                if ((rs.getString(7)).equals("Ativo")) {
                    lblStatusVend.setForeground(Color.green.darker());
                    txtIdVendedor.setEnabled(false);
                } else {
                    lblStatusVend.setForeground(Color.red);
                    
                }
                

            } else {
                JOptionPane.showMessageDialog(null, "Vendedor não localizado!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                lblNomeVend.setText(null);
                lblStatusVend.setText(null);
                txtIdVendedor.setText(null);

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNpedido = new javax.swing.JTextField();
        txtData = new javax.swing.JTextField();
        rbtOrc = new javax.swing.JRadioButton();
        rbtPedido = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        cboStatus = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        txtPesquisarCliente = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtIdCliente = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtProduto = new javax.swing.JTextField();
        spnQuantidade = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaDesc = new javax.swing.JTextArea();
        txtIdVendedor = new javax.swing.JTextField();
        btnPedAd = new javax.swing.JButton();
        btnPedCons = new javax.swing.JButton();
        btnPedAlt = new javax.swing.JButton();
        btnPedExc = new javax.swing.JButton();
        btnPedImp = new javax.swing.JButton();
        lblValorTotal = new javax.swing.JLabel();
        spnMarkup = new javax.swing.JSpinner();
        txtCusto = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        btnPesqVend = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblNomeVend = new javax.swing.JLabel();
        lblStatusVend = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Pedidos");
        setPreferredSize(new java.awt.Dimension(740, 650));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Nº Pedido:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Data:");

        txtNpedido.setEditable(false);

        txtData.setEditable(false);

        buttonGroup1.add(rbtOrc);
        rbtOrc.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rbtOrc.setText("Orçamento");
        rbtOrc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtPedido);
        rbtPedido.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rbtPedido.setText("Pedido");
        rbtPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtPedidoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(txtNpedido, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtOrc))
                .addGap(64, 64, 64)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtPedido)
                    .addComponent(jLabel2)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNpedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtOrc)
                    .addComponent(rbtPedido))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Status");

        cboStatus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aguardando Aprovação do Orçamento", "Orçamento REPROVADO", "Enviado para Produção", "Pendência na Produção", "Pronto para Entrega", "Entregue", "Pedido Cancelado" }));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pesquisar Cliente", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        jPanel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtPesquisarCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarClienteKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("*ID:");

        txtIdCliente.setEditable(false);
        txtIdCliente.setEnabled(false);

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Nome", "Telefone"
            }
        ));
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtPesquisarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(txtIdCliente)
                .addGap(38, 38, 38))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisarCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtIdCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("*Produto:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("Quantidade:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("Descrição:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("*Custo:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("*Mark-Up:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel10.setText("*ID do Vendedor:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setText("Valor Total:");

        txaDesc.setColumns(20);
        txaDesc.setRows(5);
        jScrollPane2.setViewportView(txaDesc);

        txtIdVendedor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtIdVendedorFocusLost(evt);
            }
        });

        btnPedAd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/add.png"))); // NOI18N
        btnPedAd.setToolTipText("Adicionar Pedido");
        btnPedAd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedAdActionPerformed(evt);
            }
        });

        btnPedCons.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/search.png"))); // NOI18N
        btnPedCons.setToolTipText("Consultar Pedido");
        btnPedCons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedConsActionPerformed(evt);
            }
        });

        btnPedAlt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/edit.png"))); // NOI18N
        btnPedAlt.setToolTipText("Editar Pedido");
        btnPedAlt.setEnabled(false);
        btnPedAlt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedAltActionPerformed(evt);
            }
        });

        btnPedExc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/excluir.png"))); // NOI18N
        btnPedExc.setToolTipText("Excluir Pedido");
        btnPedExc.setEnabled(false);
        btnPedExc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedExcActionPerformed(evt);
            }
        });

        btnPedImp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/print.png"))); // NOI18N
        btnPedImp.setToolTipText("Imprimir Pedido");
        btnPedImp.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPedImp.setEnabled(false);
        btnPedImp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedImpActionPerformed(evt);
            }
        });

        lblValorTotal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        spnMarkup.setValue(1);

        jButton1.setText("Limpar Campos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnPesqVend.setText("Pesquisar Vendedor");
        btnPesqVend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesqVendActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("Vendedor:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setText("Status:");

        lblNomeVend.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        lblStatusVend.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtIdVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnPesqVend))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel13)
                                                    .addComponent(jLabel12))
                                                .addGap(32, 32, 32)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lblNomeVend, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(lblStatusVend, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGap(64, 64, 64))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtCusto, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel9)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(spnMarkup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel6)
                                            .addGap(18, 18, 18)
                                            .addComponent(spnQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jButton1))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(214, 214, 214)
                                .addComponent(jLabel11)
                                .addGap(35, 35, 35)
                                .addComponent(lblValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(83, 83, 83))
            .addGroup(layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(btnPedAd)
                .addGap(37, 37, 37)
                .addComponent(btnPedCons)
                .addGap(44, 44, 44)
                .addComponent(btnPedAlt)
                .addGap(44, 44, 44)
                .addComponent(btnPedExc)
                .addGap(44, 44, 44)
                .addComponent(btnPedImp)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtCusto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(spnQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spnMarkup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(txtIdVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnPesqVend))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblNomeVend, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(lblStatusVend))
                                .addGap(50, 50, 50))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel7)
                                .addGap(123, 123, 123))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblValorTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnPedAlt)
                    .addComponent(btnPedCons)
                    .addComponent(btnPedExc)
                    .addComponent(btnPedAd)
                    .addComponent(btnPedImp))
                .addGap(56, 56, 56))
        );

        setBounds(0, 0, 749, 589);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarClienteKeyReleased
        // TODO add your handling code here:
        pesquisar_cliente();
    }//GEN-LAST:event_txtPesquisarClienteKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void rbtOrcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcActionPerformed
        // atribuindo o tipo a variável de acordo com o radio button
        tipo = "Orçamento";

    }//GEN-LAST:event_rbtOrcActionPerformed

    private void rbtPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtPedidoActionPerformed
        // atribuindo o tipo a variável de acordo com o radio button
        tipo = "Pedido";
    }//GEN-LAST:event_rbtPedidoActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // Marcar radio button ao abrir o form
        rbtOrc.setSelected(true);
        tipo = "Orçamento";

    }//GEN-LAST:event_formInternalFrameOpened

    private void btnPedAdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedAdActionPerformed
        // TODO add your handling code here:
        cadastrar_pedido();
    }//GEN-LAST:event_btnPedAdActionPerformed

    private void btnPedConsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedConsActionPerformed
        // TODO add your handling code here:
        pesquisar_pedido();
    }//GEN-LAST:event_btnPedConsActionPerformed

    private void btnPedAltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedAltActionPerformed
        // TODO add your handling code here:
        alterar_pedido();
    }//GEN-LAST:event_btnPedAltActionPerformed

    private void btnPedExcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedExcActionPerformed
        // TODO add your handling code here:
        excluir_pedido();
    }//GEN-LAST:event_btnPedExcActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        limparTelaPed();
        btnPedAd.setEnabled(true);
        btnPedExc.setEnabled(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnPedImpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedImpActionPerformed
        // TODO add your handling code here:
        imp_os();
    }//GEN-LAST:event_btnPedImpActionPerformed

    private void txtIdVendedorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtIdVendedorFocusLost
        // TODO add your handling code here:
        //JOptionPane.showMessageDialog(null, "Foco Perdido", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        //SwingUtilities.invokeLater(() -> txtIdVendedor.requestFocus());
    }//GEN-LAST:event_txtIdVendedorFocusLost

    private void btnPesqVendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesqVendActionPerformed
        // TODO add your handling code here:
        consultar_vendedor();
    }//GEN-LAST:event_btnPesqVendActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPedAd;
    private javax.swing.JButton btnPedAlt;
    private javax.swing.JButton btnPedCons;
    private javax.swing.JButton btnPedExc;
    private javax.swing.JButton btnPedImp;
    private javax.swing.JButton btnPesqVend;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblNomeVend;
    private javax.swing.JLabel lblStatusVend;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JRadioButton rbtOrc;
    private javax.swing.JRadioButton rbtPedido;
    private javax.swing.JSpinner spnMarkup;
    private javax.swing.JSpinner spnQuantidade;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextArea txaDesc;
    private javax.swing.JTextField txtCusto;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtIdCliente;
    private javax.swing.JTextField txtIdVendedor;
    private javax.swing.JTextField txtNpedido;
    private javax.swing.JTextField txtPesquisarCliente;
    private javax.swing.JTextField txtProduto;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.maiormoveis.telas;

import br.com.maiormoveis.classes.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import br.com.maiormoveis.dal.ModuloConexao;
import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author euraf
 */
public class TelaCliente extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TeleCliente
     */
    public TelaCliente() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void limparTelaCli() {
        txtCliPesquisar.setText(null);
        txtCliNome.setText(null);
        txtCliCpf.setText(null);
        txtCliEnd.setText(null);
        txtCliFone.setText(null);
        txtCliEmail.setText(null);
        lblStatusCli.setText(null);
    }

    private void cadastrar() {
        String sql = "insert into tbclientes (nomecliente, cpfcliente, endcliente, fonecliente, emailcliente) values (?, ?, ?, ?, ?)";
        try {
            Cliente cliente = new Cliente(); // Criar objeto cliente
            cliente.setNome(txtCliNome.getText());
            cliente.setCpf(txtCliCpf.getText());
            cliente.setEndereco(txtCliEnd.getText());
            cliente.setFone(txtCliFone.getText());
            cliente.setEmail(txtCliEmail.getText());
            pst = conexao.prepareStatement(sql);
            pst.setString(1, cliente.getNome());
            pst.setString(2, cliente.getCpf());
            pst.setString(3, cliente.getEndereco());
            pst.setString(4, cliente.getFone());
            pst.setString(5, cliente.getEmail());
            if ((txtCliNome.getText().isEmpty()) || (txtCliCpf.getText().isEmpty()) || (txtCliFone.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

            } else {
                int cadastrado = pst.executeUpdate();
                if (cadastrado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limparTelaCli();
                }
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "CPF já cadastrado!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void alterar() {
        String sql = "update tbclientes set nomecliente=?, cpfcliente=?, endcliente=?, fonecliente=?, emailcliente=? where idcliente=?";
        Cliente cliente = new Cliente(); // Criar objeto cliente
        cliente.setNome(txtCliNome.getText());
        cliente.setCpf(txtCliCpf.getText());
        cliente.setEndereco(txtCliEnd.getText());
        cliente.setFone(txtCliFone.getText());
        cliente.setEmail(txtCliEmail.getText());
        cliente.setId(txtCliId.getText());
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, cliente.getNome());
            pst.setString(2, cliente.getCpf());
            pst.setString(3, cliente.getEndereco());
            pst.setString(4, cliente.getFone());
            pst.setString(5, cliente.getEmail());
            pst.setString(6, cliente.getId());
            if ((txtCliNome.getText().isEmpty()) || (txtCliCpf.getText().isEmpty()) || (txtCliFone.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.INFORMATION_MESSAGE);

            } else {
                int cadastrado = pst.executeUpdate();
                if (cadastrado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados do Cliente alterados com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limparTelaCli();
                    btnCliAdd.setEnabled(true);
                    txtCliId.setText(null);
                    ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);

                }
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e2) {
            JOptionPane.showMessageDialog(null, "CPF já cadastrado!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisar_cliente() {
        String sql = "select idcliente as ID, nomecliente as Nome, cpfcliente as CPF, endcliente as Endereço, fonecliente as Telefone, emailcliente as Email from tbclientes where nomecliente like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            //Utiliza a biblioteca rs2xml.jar para preencher a tabela.
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void remover() {
        int deletar = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja desativar este cliente?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (deletar == JOptionPane.YES_OPTION) {
            String sql = "UPDATE tbclientes SET status = 'Inativo' WHERE idcliente = ?";
            Cliente cliente = new Cliente();
            cliente.setId(txtCliId.getText());
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, cliente.getId());
                int deletado = pst.executeUpdate();
                if (deletado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente desativado com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limparTelaCli();
                    txtCliId.setText(null);
                    ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
                    btnCliAdd.setEnabled(true);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

            /*
            
            String sql = "delete from tbclientes where idcliente=?";
            try {
                Cliente cliente = new Cliente(); // Criar objeto cliente
                cliente.setId(txtCliId.getText());
                pst = conexao.prepareStatement(sql);
                pst.setString(1, cliente.getId());
                int deletado = pst.executeUpdate();
                if (deletado > 0) {
                    JOptionPane.showMessageDialog(null, "Cliente excluído com sucesso!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    limparTelaCli();
                    txtCliId.setText(null);
                    ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
                    btnCliAdd.setEnabled(true);
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            
             */
        }
    }

    private void setar_campos() {
        int setar = tblClientes.getSelectedRow();
        txtCliId.setText(tblClientes.getModel().getValueAt(setar, 0).toString());
        txtCliNome.setText(tblClientes.getModel().getValueAt(setar, 1).toString());
        txtCliCpf.setText(tblClientes.getModel().getValueAt(setar, 2).toString());
        txtCliEnd.setText(tblClientes.getModel().getValueAt(setar, 3).toString());
        txtCliFone.setText(tblClientes.getModel().getValueAt(setar, 4).toString());
        txtCliEmail.setText(tblClientes.getModel().getValueAt(setar, 5).toString());
        btnCliAdd.setEnabled(false);
        
        String sql = "select status from tbclientes where idcliente=?";
        Cliente cliente = new Cliente();
        cliente.setId(tblClientes.getModel().getValueAt(setar, 0).toString());
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, cliente.getId());
            rs = pst.executeQuery();
            if (rs.next()) {
                lblStatusCli.setText(rs.getString(1));
                if ((rs.getString(1)).equals("Ativo")) {
                    lblStatusCli.setForeground(Color.green.darker());
                } else {
                    lblStatusCli.setForeground(Color.red);
                }

            } else {
                JOptionPane.showMessageDialog(null, "Usuário não localizado!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
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

        btnCliAdd = new javax.swing.JButton();
        btnCliRemov = new javax.swing.JButton();
        btnCliAlt = new javax.swing.JButton();
        txtCliFone = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCliNome = new javax.swing.JTextField();
        txtCliCpf = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtCliEnd = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCliEmail = new javax.swing.JTextField();
        txtCliPesquisar = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtCliId = new javax.swing.JTextField();
        btnLimpar = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        lblStatusCli = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Clientes");
        setPreferredSize(new java.awt.Dimension(740, 650));

        btnCliAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/create.png"))); // NOI18N
        btnCliAdd.setToolTipText("Cadastrar Cliente");
        btnCliAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliAdd.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliAddActionPerformed(evt);
            }
        });

        btnCliRemov.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/delete.png"))); // NOI18N
        btnCliRemov.setToolTipText("Desativar Cliente");
        btnCliRemov.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliRemov.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliRemov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliRemovActionPerformed(evt);
            }
        });

        btnCliAlt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/update.png"))); // NOI18N
        btnCliAlt.setToolTipText("Alterar Cliente");
        btnCliAlt.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCliAlt.setPreferredSize(new java.awt.Dimension(80, 80));
        btnCliAlt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCliAltActionPerformed(evt);
            }
        });

        txtCliFone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliFoneActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("* Campos Obrigatórios");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("*Nome:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("*Telefone:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("*CPF:");

        txtCliNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliNomeActionPerformed(evt);
            }
        });

        txtCliCpf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliCpfActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Endereço");

        txtCliEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliEndActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("E-mail:");

        txtCliEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliEmailActionPerformed(evt);
            }
        });

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/maiormoveis/icones/pesquisar.png"))); // NOI18N

        tblClientes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "CPF", "Endereço", "Telefone", "Email"
            }
        ));
        tblClientes.setFocusable(false);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("ID Cliente:");

        txtCliId.setEnabled(false);

        btnLimpar.setText("Limpar Campos");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("Status:");

        lblStatusCli.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(btnCliAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(117, 117, 117)
                        .addComponent(btnCliAlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112)
                        .addComponent(btnCliRemov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnLimpar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addGap(18, 18, 18)
                                .addComponent(txtCliEmail))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCliCpf, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCliNome)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblStatusCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel5)
                                                .addComponent(jLabel4))
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel1)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(txtCliEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(109, 109, 109))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel8)))
                        .addGap(5, 5, 5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(lblStatusCli, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(btnLimpar))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtCliCpf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtCliEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCliFone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(txtCliEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCliAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliRemov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCliAlt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        setBounds(0, 0, 749, 589);
    }// </editor-fold>//GEN-END:initComponents

    private void btnCliAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliAddActionPerformed
        cadastrar();
    }//GEN-LAST:event_btnCliAddActionPerformed

    private void btnCliRemovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliRemovActionPerformed
        // TODO add your handling code here:
        remover();

    }//GEN-LAST:event_btnCliRemovActionPerformed

    private void btnCliAltActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCliAltActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnCliAltActionPerformed

    private void txtCliFoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliFoneActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliFoneActionPerformed

    private void txtCliNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliNomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliNomeActionPerformed

    private void txtCliCpfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliCpfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliCpfActionPerformed

    private void txtCliEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliEndActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliEndActionPerformed

    private void txtCliEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliEmailActionPerformed

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // TODO add your handling code here:
        pesquisar_cliente();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // TODO add your handling code here:
        setar_campos();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        // TODO add your handling code here:
        limparTelaCli();
        txtCliId.setText(null);
        ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);
        btnCliAdd.setEnabled(true);

    }//GEN-LAST:event_btnLimparActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCliAdd;
    private javax.swing.JButton btnCliAlt;
    private javax.swing.JButton btnCliRemov;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatusCli;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliCpf;
    private javax.swing.JTextField txtCliEmail;
    private javax.swing.JTextField txtCliEnd;
    private javax.swing.JTextField txtCliFone;
    private javax.swing.JTextField txtCliId;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliPesquisar;
    // End of variables declaration//GEN-END:variables
}

import javax.swing.*;
import java.util.Map;

public class Online extends javax.swing.JFrame {

    private Map<String, String> regClients;
    private javax.swing.JButton chat_btn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<String> online_list;
    private DefaultListModel listModel;


    public Online() {
        initComponents();

        listModel = new DefaultListModel();
        online_list.setModel(listModel);
    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        online_list = new javax.swing.JList<>();
        chat_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        online_list.setFont(new java.awt.Font("Segoe UI Semilight", 0, 14));
        jScrollPane1.setViewportView(online_list);

        chat_btn.setText("Users Online");
        chat_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(116, 116, 116)
                                .addComponent(chat_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(133, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chat_btn, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String user = online_list.getSelectedValue();
        String i2p_address = regClients.get(user);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new NewJFrame(i2p_address)).setVisible(true);
            }
        });
    }

    public void updateUsers(Map<String, String> regClients) {
        // Ανανέωση της λίστας χρηστών
        this.regClients = regClients;

        DefaultListModel model = (DefaultListModel) online_list.getModel();
        // Αφαίρεση ολων των προηγούμενων χρηστών από την λίστα
        model.removeAllElements();

        // προσθήκη όλων των χρηστών στην νέα λίστα
        for (String s : regClients.keySet()) {
            listModel.addElement(s);
        }
        online_list = new JList(model);
    }

}

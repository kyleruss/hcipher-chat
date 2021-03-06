//-------------------------------------
//  Kyle Russell
//  AUT University 2016
//  Highly Secured Systems
//-------------------------------------

package com.kyleruss.hillc.client.gui;

import com.kyleruss.hillc.client.ChatConversation;
import com.kyleruss.hillc.client.ChatMessage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.miginfocom.swing.MigLayout;

public class ConversationPanel extends JPanel implements ListCellRenderer, ListSelectionListener
{
    private JList chatList;
    private DefaultListModel chatModel;
    private ControlPanel controlPanel;
    private ChatPanel parentPanel;
    
    public ConversationPanel(ChatPanel parentPanel)
    {
        setLayout(new BorderLayout());
        this.parentPanel    =   parentPanel;
        chatModel           =   new DefaultListModel();
        chatList            =   new JList(chatModel);
        controlPanel        =   new ControlPanel();
        chatList.setCellRenderer(this);
        chatList.addListSelectionListener(this);
        
        JPanel titleWrapper =   new JPanel();
        titleWrapper.setBackground(Color.WHITE);
        titleWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY));
        
        
        add(titleWrapper, BorderLayout.NORTH);
        add(chatList, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    public void addMessage(ChatMessage message)
    {
        chatModel.addElement(message);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        ChatMessage msg                         =   (ChatMessage) value;
        DefaultListCellRenderer defaultRenderer =   new DefaultListCellRenderer();
        JLabel defaultComponent                 =   (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if(msg.isServerMessage())
            defaultComponent.setIcon(new ImageIcon(AppResources.getInstance().getConnImage()));
        else
            defaultComponent.setIcon(new ImageIcon(AppResources.getInstance().getUserImage()));
        
        return defaultComponent;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) 
    {
        Object value    =   chatList.getSelectedValue();
        if(value != null)
        {
            ChatMessage chatMsg =   (ChatMessage) value;
            if(chatMsg.isServerMessage()) return;
            else
            {
                JPanel messageDisplayPanel  =   new JPanel(new MigLayout());
                messageDisplayPanel.add(new JLabel("Plain text: "));
                messageDisplayPanel.add(new JLabel(chatMsg.getContent()), "wrap");
                messageDisplayPanel.add(new JLabel("Encrypted text: "));
                messageDisplayPanel.add(new JLabel(chatMsg.getEncryptedContent()), "wrap");
                messageDisplayPanel.add(new JLabel("User: "));
                messageDisplayPanel.add(new JLabel(chatMsg.getUser()), "wrap");
                messageDisplayPanel.add(new JLabel("Time sent: "));
                messageDisplayPanel.add(new JLabel(chatMsg.getFormattedDateString()));
                
                JOptionPane.showMessageDialog(null, messageDisplayPanel);
            }
        }
    }
    
    private class ControlPanel extends JPanel implements ActionListener
    {
        private JButton sendBtn, leaveBtn;
        private JTextArea chatInputField;
        
        public ControlPanel()
        {
            setPreferredSize(new Dimension(0, 80));
            setLayout(new BorderLayout());
            
            sendBtn         =   new JButton("Send");
            leaveBtn        =   new JButton("Leave");
            chatInputField  =   new JTextArea();
            chatInputField.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
            sendBtn.setIcon(new ImageIcon(AppResources.getInstance().getChatImage()));
            leaveBtn.setIcon(new ImageIcon(AppResources.getInstance().getLeaveImage()));
            
            JPanel controlWrapper   =   new JPanel(new GridLayout(2, 1));
            controlWrapper.setPreferredSize(new Dimension(100, 0));
            
            controlWrapper.add(sendBtn);
            controlWrapper.add(leaveBtn);
            
            add(chatInputField, BorderLayout.CENTER);
            add(controlWrapper, BorderLayout.EAST);
            sendBtn.addActionListener(this);
            leaveBtn.addActionListener(this);
        }
        
        private void sendMsg()
        {
            String message                  =   chatInputField.getText();
            ChatConversation conversation   =   parentPanel.getConversation();   

            chatInputField.setText("");
            conversation.sendMessage(message);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            Object src  =   e.getSource();
            if(src == sendBtn)
                sendMsg();
            
            else if(src == leaveBtn)
                MainPanel.getInstance().leaveRoom();
        }
    }
}

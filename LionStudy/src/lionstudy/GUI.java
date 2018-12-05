package lionstudy;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import lionstudy.Classes.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Corey
 */
public class GUI extends javax.swing.JFrame {
    IRC_LiveSocket listen;
    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Res/LSIcon.png")));
    }
    
    public int logoutOnExit() {
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.logout();
        return EXIT_ON_CLOSE;
    }
    
    CurrentUser CU;
    
    //only run at very beginning
    protected void SetUpLoginScenario(){
        this.LionStudyTabs.addTab("Login",LoginTab);
        this.LionStudyTabs.addTab("Sign Up",SignUpTab);
        this.LogoutButton.setVisible(false);
        this.LionStudyTabs.remove(ChatTab);
        this.LionStudyTabs.remove(SearchTab);
        this.LionStudyTabs.remove(ProfileTab);
        this.LionStudyTabs.remove(ContactsTab);
        this.LionStudyTabs.remove(ModOptions);
        this.LionStudyTabs.remove(SuperImpTab);
    }
    //sets up the tabs for a normal user
    protected void SetUpNormalUserScenario(){
        this.LogoutButton.setVisible(true);
        this.LionStudyTabs.remove(LoginTab);
        this.LionStudyTabs.remove(SignUpTab);
        this.LionStudyTabs.addTab("Search",new ImageIcon(this.getClass().getResource("/Res/SearchImg.png")),SearchTab);
        this.LionStudyTabs.addTab("Profile",new ImageIcon(this.getClass().getResource("/Res/ProfileImg.png")),ProfileTab);
        this.LionStudyTabs.addTab("Chat",new ImageIcon(this.getClass().getResource("/Res/ChatImg.png")),ChatTab);
        this.LionStudyTabs.addTab("Contacts",new ImageIcon(this.getClass().getResource("/Res/ContactsImg.png")),ContactsTab);
        this.LionStudyTabs.addTab("Services",new ImageIcon(this.getClass().getResource("/Res/SuperImg.png")), SuperImpTab);
    }
    //sets up the tabs for a moderator user
    protected void SetUpModeratorScenario(){
        this.LogoutButton.setVisible(true);
        this.LionStudyTabs.remove(LoginTab);
        this.LionStudyTabs.remove(SignUpTab);
        this.LionStudyTabs.addTab("Search",new ImageIcon(this.getClass().getResource("/Res/SearchImg.png")),SearchTab);
        this.LionStudyTabs.addTab("Profile",new ImageIcon(this.getClass().getResource("/Res/ProfileImg.png")),ProfileTab);
        this.LionStudyTabs.addTab("Chat",new ImageIcon(this.getClass().getResource("/Res/ChatImg.png")),ChatTab);
        this.LionStudyTabs.addTab("Contacts",new ImageIcon(this.getClass().getResource("/Res/ContactsImg.png")),ContactsTab);
        this.LionStudyTabs.addTab("Services",new ImageIcon(this.getClass().getResource("/Res/SuperImg.png")), SuperImpTab);
        this.LionStudyTabs.addTab("Moderator",ModOptions);
    }
    //refreshes the contacts list in the gui
    protected void RefreshContactsList(){
       ServiceDispatcher sd = new ServiceDispatcher();
       ArrayList<Account> contacts = sd.GetAllUsersContacts();
        DefaultListModel contactsModel = new DefaultListModel();
        for (int i = 0; i < contacts.size(); i++){
            String user = contacts.get(i).getFirstName() + " " + contacts.get(i).getLastName();
            contactsModel.addElement(user);
        }
        this.contactsList.setModel(contactsModel);
   }
    
    //Logs the user in and fills all appropriate fields
    protected void Login(){
         ServiceDispatcher sd = new ServiceDispatcher();
        boolean loginSuccess = false;
        if (usernamefield.getText() == "" || passwordfield.getText() == "") {
            JOptionPane.showMessageDialog(null, "One of your fields for Username or Password is Empty", "Empty Field", JOptionPane.INFORMATION_MESSAGE);
        } else {
            loginSuccess = sd.Login(usernamefield.getText(), passwordfield.getText());
            if (loginSuccess == true) {
                //Student or Tutor
                if(CurrentUser.getBadgetype() == 1 || CurrentUser.getBadgetype() == 2 || CurrentUser.getBadgetype() == 3){
                    this.SetUpNormalUserScenario();
                    if(CurrentUser.getBadgetype() == 1){
                        this.accountTypeField.setText("Student");
                        this.badgeImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Res/StudentBadge24.png")));

                    }
                    else if(CurrentUser.getBadgetype() == 2){
                        this.accountTypeField.setText("Tutor");
                        this.badgeImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Res/TutorBadge24.png")));
                    }
                    else if(CurrentUser.getBadgetype() == 3){
                        this.badgeImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Res/TeacherBadge24.png")));
                        this.accountTypeField.setText("Teacher");
                    }
                }
                //Moderator
                else if(CurrentUser.getBadgetype() == 4){
                   this.SetUpModeratorScenario();
                   this.accountTypeField.setText("Moderator");
                   this.badgeImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Res/ModeratorBadge24.png")));
                }
                
                //Fills in the users classes on the profile tab
                ArrayList<String> userCoursesProfile = sd.GetAllUsersClasses();
                DefaultListModel classListProfile = new DefaultListModel();
                for(int x = 0; x<userCoursesProfile.size(); x++){
                   classListProfile.addElement(userCoursesProfile.get(x)+"\n");
                }
                this.courseListProfile.setModel(classListProfile);
               
                //Fills in the Profile Tab
                this.firstNameField.setText(CurrentUser.getFirstname());
                this.lastNameField.setText(CurrentUser.getLastname());
                this.usernameField.setText(CurrentUser.getUsername());
 
                
                
                //fills the combo box for courses in Search tab
                ArrayList<String> allClassesList = sd.GetAllClasses();
                this.coursesComboBox.setModel(new DefaultComboBoxModel(allClassesList.toArray()));
                ArrayList<String> cl = sd.GetAllUsersClasses();
                joinClassButton.setVisible(false);
                this.coursesComboBox.setSelectedIndex(-1);
                DefaultListModel NoModel = new DefaultListModel();
                this.onlineJList.setModel(NoModel);
                DefaultListModel ModModel = new DefaultListModel();
                ArrayList<Account> mods = sd.GetAllMods();
                for(int x = 0; x<mods.size(); x++){
                    ModModel.addElement(mods.get(x).getUsername() + ": " + mods.get(x).getFirstName() + " " + mods.get(x).getLastName());
                }
                this.modsJList.setModel(ModModel);
                
                
                //Fills in all contacts
                this.RefreshContactsList();
            } else {
                JOptionPane.showMessageDialog(null, "The username or password was incorrect, please try again", "Incorrect Username/Password", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    UserProfile clientUser = new UserProfile();
    ArrayList<UserProfile> nameList = new ArrayList();
    String sentMessagesBuffer;
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        contactsMenu = new javax.swing.JPopupMenu();
        ConnectMenuItem = new javax.swing.JMenuItem();
        RemoveMenuItem = new javax.swing.JMenuItem();
        onlineUserMenu = new javax.swing.JPopupMenu();
        ChatMenuItem = new javax.swing.JMenuItem();
        AddMenuItem = new javax.swing.JMenuItem();
        offlineUserMenu = new javax.swing.JPopupMenu();
        AddOfflineMenuItem = new javax.swing.JMenuItem();
        LionStudyTabs = new javax.swing.JTabbedPane();
        SearchTab = new javax.swing.JPanel();
        coursespanel = new javax.swing.JPanel();
        coursesComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        joinClassButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        SearchResultListsPanel = new javax.swing.JPanel();
        onlinelabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        onlineJList = new javax.swing.JList<>();
        offlinelabel = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        offlineJList = new javax.swing.JList<>();
        ChatTab = new javax.swing.JPanel();
        InteractionPanel = new javax.swing.JPanel();
        messageField = new javax.swing.JTextField();
        submitButton = new javax.swing.JButton();
        chatPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        incomeChatArea = new javax.swing.JTextArea();
        ProfileTab = new javax.swing.JPanel();
        ProfileTabCoursePanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        courseListProfile = new javax.swing.JList<>();
        removeCourseButton = new javax.swing.JButton();
        courseListPanel = new javax.swing.JPanel();
        courseListText = new javax.swing.JLabel();
        ProfileTabInfoPanel = new javax.swing.JPanel();
        badgepanel = new javax.swing.JPanel();
        badgeImg = new javax.swing.JLabel();
        profilepanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        firstNameLabel = new javax.swing.JLabel();
        firstNameField = new javax.swing.JTextField();
        lastNameLabel = new javax.swing.JLabel();
        lastNameField = new javax.swing.JTextField();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        accountTypeLabel = new javax.swing.JLabel();
        accountTypeField = new javax.swing.JTextField();
        ContactsTab = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        contactsList = new javax.swing.JList<>();
        contactspanel = new javax.swing.JPanel();
        lnameFilterField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        contactsSearchButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        SuperImpTab = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        ContactModLabel = new java.awt.Label();
        jScrollPane7 = new javax.swing.JScrollPane();
        modsJList = new javax.swing.JList<>();
        quicklinkslabel = new java.awt.Label();
        ModOptions = new javax.swing.JPanel();
        ModOptionsTab = new javax.swing.JPanel();
        AddClassName = new javax.swing.JTextField();
        ModOptionsLabel = new java.awt.Label();
        AddClassLabel = new java.awt.Label();
        addCourseModButton = new javax.swing.JButton();
        LoginTab = new javax.swing.JPanel();
        PasswordText = new javax.swing.JLabel();
        UsernameText = new javax.swing.JLabel();
        passwordfield = new javax.swing.JPasswordField();
        usernamefield = new javax.swing.JTextField();
        LoginButton = new javax.swing.JButton();
        LionStudyLoginText = new javax.swing.JLabel();
        SignUpTab = new javax.swing.JPanel();
        SignUpPanel = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        passwordfieldsignup = new javax.swing.JPasswordField();
        usernamefieldsignup = new javax.swing.JTextField();
        SignUp = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        passwordfieldsignupreenter = new javax.swing.JPasswordField();
        LionStudySignUpLabel = new javax.swing.JLabel();
        tutorRB = new javax.swing.JRadioButton();
        studentRB = new javax.swing.JRadioButton();
        professorRB = new javax.swing.JRadioButton();
        whoareyoulabel = new javax.swing.JLabel();
        Firstnametext = new javax.swing.JLabel();
        lastnametext = new javax.swing.JLabel();
        firstnamefield = new javax.swing.JTextField();
        lastnamefield = new javax.swing.JTextField();
        TopPanel = new javax.swing.JPanel();
        LionStudyImage = new javax.swing.JLabel();
        LionStudyText = new javax.swing.JLabel();
        pennstatelogolabel = new javax.swing.JLabel();
        LogoutButton = new javax.swing.JButton();
        lowerpanel = new javax.swing.JPanel();

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        contactsMenu.setMaximumSize(new java.awt.Dimension(75, 25));
        contactsMenu.setMinimumSize(new java.awt.Dimension(75, 25));
        contactsMenu.setName("[75, 25]"); // NOI18N

        ConnectMenuItem.setText("Connect");
        ConnectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectMenuItemActionPerformed(evt);
            }
        });
        contactsMenu.add(ConnectMenuItem);

        RemoveMenuItem.setText("Remove from Contacts");
        RemoveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveMenuItemActionPerformed(evt);
            }
        });
        contactsMenu.add(RemoveMenuItem);

        onlineUserMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onlineUserMenuMouseClicked(evt);
            }
        });

        ChatMenuItem.setText("Chat with user");
        ChatMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatMenuItemActionPerformed(evt);
            }
        });
        onlineUserMenu.add(ChatMenuItem);

        AddMenuItem.setText("Add to Contacts");
        AddMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddMenuItemActionPerformed(evt);
            }
        });
        onlineUserMenu.add(AddMenuItem);

        AddOfflineMenuItem.setText("Add to Contacts");
        AddOfflineMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddOfflineMenuItemActionPerformed(evt);
            }
        });
        offlineUserMenu.add(AddOfflineMenuItem);

        setDefaultCloseOperation(logoutOnExit());
        setTitle("LionStudy");
        setBackground(new java.awt.Color(0, 0, 51));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImages(null);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                WindowClosing(evt);
            }
        });

        LionStudyTabs.setBackground(new java.awt.Color(0, 0, 0));

        SearchTab.setLayout(new java.awt.BorderLayout());

        coursespanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 102), 1, true));

        coursesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coursesComboBoxActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Sylfaen", 1, 18)); // NOI18N
        jLabel1.setText("Courses");

        joinClassButton.setText("Join Class");
        joinClassButton.setToolTipText("");
        joinClassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinClassButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Georgia", 1, 11)); // NOI18N
        jLabel2.setText("*If course is unlisted, please contact a Moderator from Services*");

        javax.swing.GroupLayout coursespanelLayout = new javax.swing.GroupLayout(coursespanel);
        coursespanel.setLayout(coursespanelLayout);
        coursespanelLayout.setHorizontalGroup(
            coursespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coursespanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(coursespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(coursespanelLayout.createSequentialGroup()
                        .addComponent(coursesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(joinClassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(coursespanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(195, 195, 195)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, coursespanelLayout.createSequentialGroup()
                .addContainerGap(194, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(144, 144, 144))
        );
        coursespanelLayout.setVerticalGroup(
            coursespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(coursespanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(coursespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(coursespanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(coursespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(coursesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(joinClassButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        SearchTab.add(coursespanel, java.awt.BorderLayout.PAGE_START);

        onlinelabel.setFont(new java.awt.Font("Tw Cen MT Condensed", 1, 24)); // NOI18N
        onlinelabel.setText("Online");
        onlinelabel.setMaximumSize(new java.awt.Dimension(50, 25));
        onlinelabel.setMinimumSize(new java.awt.Dimension(50, 25));

        onlineJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onlineJListMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onlineJListMouseReleased(evt);
            }
        });
        onlineJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                onlineJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(onlineJList);

        offlinelabel.setFont(new java.awt.Font("Tw Cen MT Condensed", 1, 24)); // NOI18N
        offlinelabel.setText("Offline");
        offlinelabel.setMaximumSize(new java.awt.Dimension(50, 25));
        offlinelabel.setMinimumSize(new java.awt.Dimension(50, 25));

        offlineJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                offlineJListMouseReleased(evt);
            }
        });
        jScrollPane6.setViewportView(offlineJList);

        javax.swing.GroupLayout SearchResultListsPanelLayout = new javax.swing.GroupLayout(SearchResultListsPanel);
        SearchResultListsPanel.setLayout(SearchResultListsPanelLayout);
        SearchResultListsPanelLayout.setHorizontalGroup(
            SearchResultListsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane6)
            .addGroup(SearchResultListsPanelLayout.createSequentialGroup()
                .addGroup(SearchResultListsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(onlinelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(offlinelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 687, Short.MAX_VALUE))
        );
        SearchResultListsPanelLayout.setVerticalGroup(
            SearchResultListsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SearchResultListsPanelLayout.createSequentialGroup()
                .addComponent(onlinelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(offlinelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        SearchTab.add(SearchResultListsPanel, java.awt.BorderLayout.CENTER);

        LionStudyTabs.addTab("Search", new javax.swing.ImageIcon(getClass().getResource("/Res/SearchImg.png")), SearchTab); // NOI18N

        ChatTab.setLayout(new java.awt.BorderLayout());

        InteractionPanel.setBackground(new java.awt.Color(6, 6, 50));
        InteractionPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        messageField.setForeground(new java.awt.Color(0, 0, 102));
        messageField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        messageField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageFieldActionPerformed(evt);
            }
        });

        submitButton.setBackground(new java.awt.Color(0, 0, 51));
        submitButton.setText("Submit");
        submitButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        submitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout InteractionPanelLayout = new javax.swing.GroupLayout(InteractionPanel);
        InteractionPanel.setLayout(InteractionPanelLayout);
        InteractionPanelLayout.setHorizontalGroup(
            InteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InteractionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(submitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );
        InteractionPanelLayout.setVerticalGroup(
            InteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InteractionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InteractionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(messageField, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(submitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(101, Short.MAX_VALUE))
        );

        ChatTab.add(InteractionPanel, java.awt.BorderLayout.PAGE_END);

        chatPanel.setLayout(new java.awt.GridLayout(1, 0));

        chatTextArea.setColumns(20);
        chatTextArea.setRows(5);
        jScrollPane5.setViewportView(chatTextArea);

        chatPanel.add(jScrollPane5);

        incomeChatArea.setColumns(20);
        incomeChatArea.setRows(5);
        jScrollPane2.setViewportView(incomeChatArea);

        chatPanel.add(jScrollPane2);

        ChatTab.add(chatPanel, java.awt.BorderLayout.CENTER);

        LionStudyTabs.addTab("Chat", new javax.swing.ImageIcon(getClass().getResource("/Res/ChatImg.png")), ChatTab); // NOI18N

        ProfileTab.setLayout(new java.awt.BorderLayout());

        ProfileTabCoursePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 102)));

        jScrollPane4.setViewportView(courseListProfile);

        removeCourseButton.setText("Remove Course");
        removeCourseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCourseButtonActionPerformed(evt);
            }
        });

        courseListText.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        courseListText.setForeground(new java.awt.Color(0, 0, 51));
        courseListText.setText("Course List");

        javax.swing.GroupLayout courseListPanelLayout = new javax.swing.GroupLayout(courseListPanel);
        courseListPanel.setLayout(courseListPanelLayout);
        courseListPanelLayout.setHorizontalGroup(
            courseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(courseListPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(courseListText)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        courseListPanelLayout.setVerticalGroup(
            courseListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(courseListPanelLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(courseListText)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ProfileTabCoursePanelLayout = new javax.swing.GroupLayout(ProfileTabCoursePanel);
        ProfileTabCoursePanel.setLayout(ProfileTabCoursePanelLayout);
        ProfileTabCoursePanelLayout.setHorizontalGroup(
            ProfileTabCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(courseListPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(ProfileTabCoursePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(removeCourseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ProfileTabCoursePanelLayout.setVerticalGroup(
            ProfileTabCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ProfileTabCoursePanelLayout.createSequentialGroup()
                .addComponent(courseListPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(removeCourseButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ProfileTab.add(ProfileTabCoursePanel, java.awt.BorderLayout.LINE_END);

        ProfileTabInfoPanel.setLayout(new java.awt.BorderLayout());

        badgeImg.setMaximumSize(new java.awt.Dimension(84, 84));
        badgeImg.setMinimumSize(new java.awt.Dimension(84, 84));

        javax.swing.GroupLayout badgepanelLayout = new javax.swing.GroupLayout(badgepanel);
        badgepanel.setLayout(badgepanelLayout);
        badgepanelLayout.setHorizontalGroup(
            badgepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(badgepanelLayout.createSequentialGroup()
                .addGap(195, 195, 195)
                .addComponent(badgeImg, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );
        badgepanelLayout.setVerticalGroup(
            badgepanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, badgepanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(badgeImg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ProfileTabInfoPanel.add(badgepanel, java.awt.BorderLayout.PAGE_START);

        profilepanel.setLayout(new java.awt.GridLayout(8, 3, 0, 20));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        profilepanel.add(jPanel9);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        profilepanel.add(jPanel10);

        firstNameLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        firstNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        firstNameLabel.setText("First Name:          ");
        firstNameLabel.setToolTipText("");
        profilepanel.add(firstNameLabel);

        firstNameField.setEditable(false);
        firstNameField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        firstNameField.setText(CurrentUser.getFirstname());
        firstNameField.setToolTipText("");
        firstNameField.setBorder(null);
        profilepanel.add(firstNameField);

        lastNameLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lastNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lastNameLabel.setText("Last Name:          ");
        lastNameLabel.setToolTipText("");
        profilepanel.add(lastNameLabel);

        lastNameField.setEditable(false);
        lastNameField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lastNameField.setText("jTextField2");
        lastNameField.setBorder(null);
        profilepanel.add(lastNameField);

        usernameLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        usernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        usernameLabel.setText("Username:          ");
        profilepanel.add(usernameLabel);

        usernameField.setEditable(false);
        usernameField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        usernameField.setText("usernameField");
        usernameField.setBorder(null);
        profilepanel.add(usernameField);

        accountTypeLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        accountTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        accountTypeLabel.setText("Account Type:          ");
        accountTypeLabel.setToolTipText("");
        profilepanel.add(accountTypeLabel);

        accountTypeField.setEditable(false);
        accountTypeField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        accountTypeField.setText("jTextField4");
        accountTypeField.setBorder(null);
        profilepanel.add(accountTypeField);

        ProfileTabInfoPanel.add(profilepanel, java.awt.BorderLayout.CENTER);

        ProfileTab.add(ProfileTabInfoPanel, java.awt.BorderLayout.CENTER);

        LionStudyTabs.addTab("Profile", new javax.swing.ImageIcon(getClass().getResource("/Res/ProfileImg.png")), ProfileTab); // NOI18N

        ContactsTab.setLayout(new java.awt.BorderLayout());

        contactsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                contactsListMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(contactsList);

        ContactsTab.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        contactspanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 51, 102), 1, true));

        lnameFilterField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lnameFilterFieldActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
        jLabel6.setText("Last Name:");

        contactsSearchButton.setBackground(new java.awt.Color(255, 255, 255));
        contactsSearchButton.setFont(new java.awt.Font("Segoe UI Semibold", 1, 12)); // NOI18N
        contactsSearchButton.setText("Search");
        contactsSearchButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 102), 2));
        contactsSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contactsSearchButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Sylfaen", 1, 18)); // NOI18N
        jLabel4.setText("Contacts");

        javax.swing.GroupLayout contactspanelLayout = new javax.swing.GroupLayout(contactspanel);
        contactspanel.setLayout(contactspanelLayout);
        contactspanelLayout.setHorizontalGroup(
            contactspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactspanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contactspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(contactspanelLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lnameFilterField, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contactsSearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(443, Short.MAX_VALUE))
        );
        contactspanelLayout.setVerticalGroup(
            contactspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contactspanelLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(contactspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(contactsSearchButton)
                    .addGroup(contactspanelLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGroup(contactspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(contactspanelLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel6))
                            .addGroup(contactspanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(lnameFilterField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        ContactsTab.add(contactspanel, java.awt.BorderLayout.PAGE_START);

        LionStudyTabs.addTab("Contacts", new javax.swing.ImageIcon(getClass().getResource("/Res/ContactsImg.png")), ContactsTab); // NOI18N

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setForeground(new java.awt.Color(0, 0, 204));
        jButton1.setText("LionPath");

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setForeground(new java.awt.Color(0, 0, 204));
        jButton2.setText("Behrend");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setForeground(new java.awt.Color(0, 0, 204));
        jButton3.setText("Health");

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setForeground(new java.awt.Color(0, 0, 204));
        jButton4.setText("Football");

        jButton5.setBackground(new java.awt.Color(0, 0, 0));
        jButton5.setForeground(new java.awt.Color(0, 0, 204));
        jButton5.setText("Canvas");

        jButton6.setBackground(new java.awt.Color(0, 0, 0));
        jButton6.setForeground(new java.awt.Color(0, 0, 204));
        jButton6.setText("Junker");

        jButton7.setBackground(new java.awt.Color(0, 0, 0));
        jButton7.setForeground(new java.awt.Color(0, 0, 204));
        jButton7.setText("News");

        jButton8.setBackground(new java.awt.Color(0, 0, 0));
        jButton8.setForeground(new java.awt.Color(0, 0, 204));
        jButton8.setText("Weather");

        jButton9.setBackground(new java.awt.Color(0, 0, 0));
        jButton9.setForeground(new java.awt.Color(0, 0, 204));
        jButton9.setText("RAP's");

        ContactModLabel.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        ContactModLabel.setName(""); // NOI18N
        ContactModLabel.setText("Contact a Moderator");

        jScrollPane7.setViewportView(modsJList);

        quicklinkslabel.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        quicklinkslabel.setName(""); // NOI18N
        quicklinkslabel.setText("Quick Links");

        javax.swing.GroupLayout SuperImpTabLayout = new javax.swing.GroupLayout(SuperImpTab);
        SuperImpTab.setLayout(SuperImpTabLayout);
        SuperImpTabLayout.setHorizontalGroup(
            SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SuperImpTabLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ContactModLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66))
            .addGroup(SuperImpTabLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SuperImpTabLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(SuperImpTabLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
            .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(SuperImpTabLayout.createSequentialGroup()
                    .addGap(156, 156, 156)
                    .addComponent(quicklinkslabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(498, Short.MAX_VALUE)))
        );
        SuperImpTabLayout.setVerticalGroup(
            SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SuperImpTabLayout.createSequentialGroup()
                .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(SuperImpTabLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(ContactModLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SuperImpTabLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(121, Short.MAX_VALUE))
            .addGroup(SuperImpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(SuperImpTabLayout.createSequentialGroup()
                    .addGap(78, 78, 78)
                    .addComponent(quicklinkslabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(373, Short.MAX_VALUE)))
        );

        LionStudyTabs.addTab("Important Services", new javax.swing.ImageIcon(getClass().getResource("/Res/SuperImg.png")), SuperImpTab); // NOI18N

        ModOptionsLabel.setFont(new java.awt.Font("Bell MT", 0, 36)); // NOI18N
        ModOptionsLabel.setText("Moderator Options");

        AddClassLabel.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        AddClassLabel.setText("Course Name:");

        addCourseModButton.setText("Add Course");
        addCourseModButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCourseModButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ModOptionsTabLayout = new javax.swing.GroupLayout(ModOptionsTab);
        ModOptionsTab.setLayout(ModOptionsTabLayout);
        ModOptionsTabLayout.setHorizontalGroup(
            ModOptionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModOptionsTabLayout.createSequentialGroup()
                .addGroup(ModOptionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModOptionsTabLayout.createSequentialGroup()
                        .addGap(231, 231, 231)
                        .addComponent(ModOptionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ModOptionsTabLayout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(AddClassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddClassName, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addCourseModButton, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(219, Short.MAX_VALUE))
        );
        ModOptionsTabLayout.setVerticalGroup(
            ModOptionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ModOptionsTabLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(ModOptionsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(ModOptionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ModOptionsTabLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(AddClassLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ModOptionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(addCourseModButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(AddClassName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(335, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout ModOptionsLayout = new javax.swing.GroupLayout(ModOptions);
        ModOptions.setLayout(ModOptionsLayout);
        ModOptionsLayout.setHorizontalGroup(
            ModOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 776, Short.MAX_VALUE)
            .addGroup(ModOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ModOptionsLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ModOptionsTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        ModOptionsLayout.setVerticalGroup(
            ModOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
            .addGroup(ModOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ModOptionsLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ModOptionsTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        LionStudyTabs.addTab("Moderator Options", ModOptions);

        PasswordText.setText("Password:");

        UsernameText.setText("Username:");

        passwordfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passwordfieldKeyPressed(evt);
            }
        });

        usernamefield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernamefieldActionPerformed(evt);
            }
        });
        usernamefield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                usernamefieldKeyPressed(evt);
            }
        });

        LoginButton.setText("Login");
        LoginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoginButtonActionPerformed(evt);
            }
        });

        LionStudyLoginText.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        LionStudyLoginText.setText("LionStudy Login");

        javax.swing.GroupLayout LoginTabLayout = new javax.swing.GroupLayout(LoginTab);
        LoginTab.setLayout(LoginTabLayout);
        LoginTabLayout.setHorizontalGroup(
            LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginTabLayout.createSequentialGroup()
                .addGroup(LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(LoginTabLayout.createSequentialGroup()
                        .addGap(246, 246, 246)
                        .addComponent(LionStudyLoginText))
                    .addGroup(LoginTabLayout.createSequentialGroup()
                        .addGap(213, 213, 213)
                        .addGroup(LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(UsernameText)
                            .addComponent(PasswordText))
                        .addGap(18, 18, 18)
                        .addGroup(LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(passwordfield, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(usernamefield, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(LoginTabLayout.createSequentialGroup()
                        .addGap(295, 295, 295)
                        .addComponent(LoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(315, Short.MAX_VALUE))
        );
        LoginTabLayout.setVerticalGroup(
            LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LoginTabLayout.createSequentialGroup()
                .addGap(97, 97, 97)
                .addComponent(LionStudyLoginText, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UsernameText)
                    .addComponent(usernamefield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(LoginTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PasswordText))
                .addGap(42, 42, 42)
                .addComponent(LoginButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        PasswordText.getAccessibleContext().setAccessibleName("passwordtext");

        LionStudyTabs.addTab("Login", LoginTab);

        jLabel17.setText("Password:");

        jLabel18.setText("Username:");

        usernamefieldsignup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usernamefieldsignupActionPerformed(evt);
            }
        });

        SignUp.setText("Sign Up");
        SignUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SignUpActionPerformed(evt);
            }
        });

        jLabel19.setText("Re-Enter Password:");

        LionStudySignUpLabel.setFont(new java.awt.Font("Verdana", 1, 24)); // NOI18N
        LionStudySignUpLabel.setText("LionStudy Sign-Up");

        buttonGroup1.add(tutorRB);
        tutorRB.setText("Tutor");

        buttonGroup1.add(studentRB);
        studentRB.setSelected(true);
        studentRB.setText("Student");

        buttonGroup1.add(professorRB);
        professorRB.setText("Professor");

        whoareyoulabel.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        whoareyoulabel.setText("Who are you?");

        Firstnametext.setText("First Name:");

        lastnametext.setText("Last Name:");

        firstnamefield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstnamefieldActionPerformed(evt);
            }
        });

        lastnamefield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastnamefieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SignUpPanelLayout = new javax.swing.GroupLayout(SignUpPanel);
        SignUpPanel.setLayout(SignUpPanelLayout);
        SignUpPanelLayout.setHorizontalGroup(
            SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SignUpPanelLayout.createSequentialGroup()
                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SignUpPanelLayout.createSequentialGroup()
                        .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SignUpPanelLayout.createSequentialGroup()
                                .addGap(209, 209, 209)
                                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel18)))
                            .addGroup(SignUpPanelLayout.createSequentialGroup()
                                .addGap(147, 147, 147)
                                .addComponent(jLabel19)))
                        .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(SignUpPanelLayout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(passwordfieldsignupreenter, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(passwordfieldsignup, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(usernamefieldsignup, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(SignUp, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(SignUpPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(professorRB)
                                    .addGroup(SignUpPanelLayout.createSequentialGroup()
                                        .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(studentRB)
                                            .addComponent(tutorRB))
                                        .addGap(44, 44, 44)
                                        .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lastnametext)
                                            .addComponent(Firstnametext))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(firstnamefield, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lastnamefield, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(SignUpPanelLayout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(whoareyoulabel))))
                    .addGroup(SignUpPanelLayout.createSequentialGroup()
                        .addGap(240, 240, 240)
                        .addComponent(LionStudySignUpLabel)))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        SignUpPanelLayout.setVerticalGroup(
            SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SignUpPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(LionStudySignUpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernamefieldsignup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(27, 27, 27)
                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(passwordfieldsignup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(passwordfieldsignupreenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addComponent(whoareyoulabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SignUpPanelLayout.createSequentialGroup()
                        .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(studentRB)
                            .addComponent(Firstnametext))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(SignUpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tutorRB)
                            .addComponent(lastnametext)
                            .addComponent(lastnamefield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(professorRB))
                    .addComponent(firstnamefield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addComponent(SignUp, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        jLabel19.getAccessibleContext().setAccessibleName("Re-EnterPassword:");

        javax.swing.GroupLayout SignUpTabLayout = new javax.swing.GroupLayout(SignUpTab);
        SignUpTab.setLayout(SignUpTabLayout);
        SignUpTabLayout.setHorizontalGroup(
            SignUpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 776, Short.MAX_VALUE)
            .addGroup(SignUpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(SignUpTabLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(SignUpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        SignUpTabLayout.setVerticalGroup(
            SignUpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 505, Short.MAX_VALUE)
            .addGroup(SignUpTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(SignUpTabLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(SignUpPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        LionStudyTabs.addTab("Sign Up", SignUpTab);

        TopPanel.setBackground(new java.awt.Color(6, 6, 50));
        TopPanel.setForeground(new java.awt.Color(255, 255, 255));

        LionStudyImage.setBackground(new java.awt.Color(0, 0, 51));

        LionStudyText.setFont(new java.awt.Font("Arabic Typesetting", 0, 48)); // NOI18N
        LionStudyText.setForeground(new java.awt.Color(244, 244, 238));
        LionStudyText.setText("LionStudy");

        pennstatelogolabel.setForeground(new java.awt.Color(255, 255, 255));
        pennstatelogolabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Res/PennState.png"))); // NOI18N

        LogoutButton.setText("Logout");
        LogoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TopPanelLayout = new javax.swing.GroupLayout(TopPanel);
        TopPanel.setLayout(TopPanelLayout);
        TopPanelLayout.setHorizontalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LogoutButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LionStudyText)
                .addGap(134, 134, 134)
                .addComponent(LionStudyImage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pennstatelogolabel))
        );
        TopPanelLayout.setVerticalGroup(
            TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LionStudyImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(LogoutButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addGroup(TopPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TopPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(pennstatelogolabel, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                    .addGroup(TopPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(LionStudyText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        LionStudyImage.getAccessibleContext().setAccessibleName("LionStudyImage");

        lowerpanel.setBackground(new java.awt.Color(6, 6, 50));

        javax.swing.GroupLayout lowerpanelLayout = new javax.swing.GroupLayout(lowerpanel);
        lowerpanel.setLayout(lowerpanelLayout);
        lowerpanelLayout.setHorizontalGroup(
            lowerpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        lowerpanelLayout.setVerticalGroup(
            lowerpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 91, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LionStudyTabs)
            .addComponent(TopPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lowerpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TopPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LionStudyTabs, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lowerpanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void receiveMSG(String username, String message){
        
        Font f = new Font(Font.SERIF, Font.BOLD, 14);
        incomeChatArea.append("---" + username + "---\n" + message);
        
        chatTextArea.setFont(f);
        chatTextArea.append("\n\n\n");
        
    }
    
    private void messageFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_messageFieldActionPerformed

    private void submitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitButtonActionPerformed
        // This button handles a chat message submission
        
        Font f = new Font(Font.SERIF, Font.BOLD, 14);
        String message = "";
        message = messageField.getText();
        
        /*
        Displays given text to the outgoing message Text Area
        */
        chatTextArea.append("\n---");
        chatTextArea.setFont(f);
        chatTextArea.append(CU.getUsername());
        chatTextArea.append("---\n");
        chatTextArea.append(message);
        chatTextArea.append("\n");
        
        /*
        Formats incoming chat message field to fit the form of a chat dialog
        */
        incomeChatArea.setFont(f);
        incomeChatArea.append("\n\n\n");
        
        
        messageField.setText("");
        
        listen.IRC_privMSG(message);
    }//GEN-LAST:event_submitButtonActionPerformed

    private void coursesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coursesComboBoxActionPerformed
        try{
        String selection = coursesComboBox.getSelectedItem().toString();
        boolean CUinclass = false;
        boolean Joined = false;
        ServiceDispatcher dispatcher = new ServiceDispatcher();
        ArrayList<Account> allUsers = dispatcher.GetUsersFromClass(selection);
        DefaultListModel OfflineModel = new DefaultListModel();
        DefaultListModel OnlineModel = new DefaultListModel();
        
        //obtains all the online  and offline users that have selected class in their courseList
        for (int i = 0; i < allUsers.size(); i++){
            String user = allUsers.get(i).getFirstName() + " " + allUsers.get(i).getLastName();
            if(CurrentUser.getUsername().equals(allUsers.get(i).getUsername())){        //checking to make you current user isnt displayed
               CUinclass = true;
               Joined = true;
            }
            if(allUsers.get(i).getOnline() == 1 && CUinclass == false){
                OnlineModel.addElement(user);
            }
            else if (allUsers.get(i).getOnline() == 0 && CUinclass == false){
                OfflineModel.addElement(user);
            }
            CUinclass = false;
        }
        onlineJList.setModel(OnlineModel);
        offlineJList.setModel(OfflineModel);
        
        
         if(Joined == true){
            joinClassButton.setVisible(false);
        }
         else{
            joinClassButton.setVisible(true);
        }
        }
        catch(NullPointerException e){
            
        }

    }//GEN-LAST:event_coursesComboBoxActionPerformed

    private void usernamefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernamefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernamefieldActionPerformed

    private void LoginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoginButtonActionPerformed
        this.Login();
    }//GEN-LAST:event_LoginButtonActionPerformed

    private void usernamefieldsignupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernamefieldsignupActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usernamefieldsignupActionPerformed

    private void SignUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SignUpActionPerformed
        int badgetemp = 0;
        boolean login = false;
        //gets last 8 digits which is @psu.edu
        String psucheck = usernamefieldsignup.getText().substring(Math.max(usernamefieldsignup.getText().length() - 8, 0));
        psucheck = psucheck.toLowerCase();
        //User left something null
        if ("".equals(usernamefieldsignup.getText()) || "".equals(passwordfieldsignupreenter.getText()) || "".equals(passwordfieldsignup.getText()) || "".equals(firstnamefield.getText()) || "".equals(lastnamefield.getText())) {
            JOptionPane.showMessageDialog(null, "One or more fields were left empty, please try again", "Empty Field", JOptionPane.INFORMATION_MESSAGE);
        } else if (!"@psu.edu".equals(psucheck)) {
            JOptionPane.showMessageDialog(null, "The username must be a penn state email, meaning an email @psu.edu", "Non-Penn State User", JOptionPane.INFORMATION_MESSAGE);
        } //if everything checks out
        else {
            if (passwordfieldsignup.getText() == null ? passwordfieldsignupreenter.getText() != null : !passwordfieldsignup.getText().equals(passwordfieldsignupreenter.getText())) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!", "Non-Matching Passwords", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (studentRB.isSelected()) {
                    badgetemp = 1;
                } else if (tutorRB.isSelected()) {
                    badgetemp = 2;
                } else if (professorRB.isSelected()) {
                    badgetemp = 3;
                }
                Account signup = new Account(usernamefieldsignup.getText(), passwordfieldsignup.getText(), firstnamefield.getText(), lastnamefield.getText(), badgetemp, 0);
                ServiceDispatcher sd = new ServiceDispatcher();
                boolean userexists = sd.CreateUser(signup);
                if(userexists == true){
                    JOptionPane.showMessageDialog(null, "Username already exists!", "Signup Failure", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                  JOptionPane.showMessageDialog(null, "Account Created!", "Signup Success", JOptionPane.INFORMATION_MESSAGE);
                  this.LionStudyTabs.setSelectedComponent(LoginTab);
                }
            }
        }
    }//GEN-LAST:event_SignUpActionPerformed

    private void firstnamefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstnamefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_firstnamefieldActionPerformed

    private void lastnamefieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastnamefieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lastnamefieldActionPerformed

    private void LogoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutButtonActionPerformed
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.logout();
        this.SetUpLoginScenario();
        chatTextArea.setText("");
    }//GEN-LAST:event_LogoutButtonActionPerformed

    private void removeCourseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCourseButtonActionPerformed
        ServiceDispatcher sd = new ServiceDispatcher();
        String course = courseListProfile.getSelectedValue();
        String[] courseToRemove = course.split("\n", 0);
        course = courseToRemove[0];
        String dialogMsg = course + " Removed Successfully!";        
        sd.DeleteClassfromUser(course);
        JOptionPane.showMessageDialog(null, dialogMsg, "Success", JOptionPane.INFORMATION_MESSAGE);
        ArrayList<String> userCoursesProfile = sd.GetAllUsersClasses();
        DefaultListModel classListProfile = new DefaultListModel();
        for(int x = 0; x<userCoursesProfile.size(); x++){
            classListProfile.addElement(userCoursesProfile.get(x)+"\n");
        }
        this.courseListProfile.setModel(classListProfile);
    }//GEN-LAST:event_removeCourseButtonActionPerformed

    private void joinClassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinClassButtonActionPerformed
        String selection = coursesComboBox.getSelectedItem().toString();
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.AddClasstoUser(selection);
        
        JOptionPane.showMessageDialog(null, "Course added.");
        joinClassButton.setVisible(false);
        coursespanel.repaint();
        
        ArrayList<String> userCoursesProfile = sd.GetAllUsersClasses();
        DefaultListModel classListProfile = new DefaultListModel();
        for(int x = 0; x<userCoursesProfile.size(); x++){
            classListProfile.addElement(userCoursesProfile.get(x)+"\n");
        }
        this.courseListProfile.setModel(classListProfile);
         
        
    }//GEN-LAST:event_joinClassButtonActionPerformed

    private void addCourseModButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCourseModButtonActionPerformed
        ServiceDispatcher sd = new ServiceDispatcher();
        String classname = this.AddClassName.getText();
        boolean success = sd.CreateClass(classname);
        if(success){
            JOptionPane.showMessageDialog(null, "Class Added", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.AddClassName.setText("");
        }
        else{
            JOptionPane.showMessageDialog(null, "Class Not Added", "Failure", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_addCourseModButtonActionPerformed

    private void onlineJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_onlineJListValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_onlineJListValueChanged

    private void onlineJListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onlineJListMouseClicked
        // TODO add your handling code here:
        //JOptionPane.showConfirmDialog(null, "Add user as contact?", "", JOptionPane.OK_CANCEL_OPTION);
        //JOptionPane.showConfirmDialog(null, "Chat with this user?", "", JOptionPane.OK_CANCEL_OPTION);
    }//GEN-LAST:event_onlineJListMouseClicked

    private void contactsListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contactsListMouseReleased
        if(evt.isPopupTrigger()){
            contactsMenu.show(this,evt.getX(), evt.getY() + 260);
        }
    }//GEN-LAST:event_contactsListMouseReleased

    private void onlineJListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onlineJListMouseReleased
        
        if(evt.isPopupTrigger()){
            onlineUserMenu.show(this,evt.getX(), evt.getY() + 300);
        }
        
    }//GEN-LAST:event_onlineJListMouseReleased

    private void offlineJListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_offlineJListMouseReleased
        
        if(evt.isPopupTrigger()){
            offlineUserMenu.show(this,evt.getX(), evt.getY() + 520);
        }
        
    }//GEN-LAST:event_offlineJListMouseReleased

    private void onlineUserMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onlineUserMenuMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_onlineUserMenuMouseClicked

    private void AddOfflineMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddOfflineMenuItemActionPerformed
        boolean flag = false;
        this.RefreshContactsList();
        for(int x = 0; x < contactsList.getModel().getSize(); x++){
            if(this.contactsList.getModel().getElementAt(x).equals(offlineJList.getSelectedValue())){
                flag = true;
                break;
            }
        }
        if(flag){
            JOptionPane.showMessageDialog(null, "Already a contact", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.AddUserContact(offlineJList.getSelectedValue());
        this.RefreshContactsList();
        }
    }//GEN-LAST:event_AddOfflineMenuItemActionPerformed

    private void AddMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddMenuItemActionPerformed
        boolean flag = false;
        this.RefreshContactsList();
        for(int x = 0; x < contactsList.getModel().getSize(); x++){
            if(this.contactsList.getModel().getElementAt(x).equals(onlineJList.getSelectedValue())){
                flag = true;
                break;
            }
        }
        if(flag){
            JOptionPane.showMessageDialog(null, "Already a contact", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        else{
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.AddUserContact(onlineJList.getSelectedValue());
        this.RefreshContactsList();
        }
    }//GEN-LAST:event_AddMenuItemActionPerformed

    private void RemoveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveMenuItemActionPerformed
        
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.RemoveUserContact(contactsList.getSelectedValue());
        
        this.RefreshContactsList();
        
    }//GEN-LAST:event_RemoveMenuItemActionPerformed

    private void lnameFilterFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lnameFilterFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lnameFilterFieldActionPerformed

    private void contactsSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contactsSearchButtonActionPerformed
        ServiceDispatcher sd = new ServiceDispatcher();
        ArrayList<Account> contacts = sd.GetAllUsersContacts();
        DefaultListModel contactsModel = new DefaultListModel();
        String SLname = lnameFilterField.getText().toLowerCase();
        if (SLname.equals("")) {
            this.RefreshContactsList();
        }
        else {
            contactsModel.removeAllElements();
            for (int x = 0; x < contacts.size(); x++) {
                String lname = contacts.get(x).getLastName().toLowerCase();
                if (lname.contains(SLname.trim())) {
                    String fullname = contacts.get(x).getFirstName() + " " + contacts.get(x).getLastName();
                    contactsModel.addElement(fullname);
                }
            }
            this.contactsList.setModel(contactsModel);
        }
    }//GEN-LAST:event_contactsSearchButtonActionPerformed

    private void ConnectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectMenuItemActionPerformed
        
        
        
        
        
        
        
    }//GEN-LAST:event_ConnectMenuItemActionPerformed

    private void ChatMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatMenuItemActionPerformed
        listen = new IRC_LiveSocket("#LionStudy",CurrentUser.getFirstname(),CurrentUser.getUsername());
        Thread chatListen = new Thread(listen);
        chatListen.start();
        
        
        
        
        
        
    }//GEN-LAST:event_ChatMenuItemActionPerformed

    private void WindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_WindowClosing
        ServiceDispatcher sd = new ServiceDispatcher();
        sd.logout();
    }//GEN-LAST:event_WindowClosing

    private void usernamefieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_usernamefieldKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            this.Login();
        }        
    }//GEN-LAST:event_usernamefieldKeyPressed

    private void passwordfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordfieldKeyPressed
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            this.Login();
        }        
    }//GEN-LAST:event_passwordfieldKeyPressed

    /**
     * @param args the command line arguments
     */
    
    
    public class IRC_LiveSocket implements Runnable
    {




        Socket lionsocket;
        OutputStream outStream;
        //Replace this with private message?
        String channelJoined;
        //Used to clear field
        final static String CRLF = "\r\n";
        //Message to be sent to output stream
        String msg;

        String server="halcyon.il.us.dal.net";
        int port=6666;


        public IRC_LiveSocket(String channel, String fName, String uName)
        {

            try
            {
            //Connects to Lionstudy Server
                lionsocket = new Socket(server, port);
            //Sets output stream to out
                outStream = lionsocket.getOutputStream();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            IRC_nick(fName);
            IRC_user(uName, "null","null","real name");
            IRC_channelJoin(channel);



        }

        private void send(String text)
        {
            byte[] bytes = (text + CRLF).getBytes();

            try
            {
                outStream.write(bytes);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        public void IRC_nick(String nick)
        {
            msg="NICK "+nick;
            send(msg);
        }

        public void IRC_user(String username, String host, String server, String realname)
        {
            msg="USER "+username+" "+host+" "+server+" :"+realname;
            send(msg);
        }

        public void IRC_channelJoin(String channel)
        {
            channelJoined=channel;
            msg="JOIN "+channelJoined;
            send(msg);
        }

        public void IRC_channelLeave(String channel)
        {
            msg="PART "+channel;
        }

        public void IRC_privMSG(String text)
        {
            msg="PRIVMSG "+channelJoined+" :"+text;
            send(msg);
        }

        void IRC_processMessage(String ircMessage)
        {
            IRC_RecievedMessage RcvMsg = IRC_MessageParser.recieved(ircMessage);
            if(RcvMsg.command.equals("PRIVMSG"))
            {
                receiveMSG(RcvMsg.nick,RcvMsg.content);
            }
            else if(RcvMsg.command.equals("CLOSE"))
            {

            }

        }

        public void run()
        {
            do{
            //LOOP TO RECIEVE MESSAGES
                try
                {
                    InputStream inStream = lionsocket.getInputStream();
                    IRC_MessageBuffer msgBuf = new IRC_MessageBuffer();

                    byte[] buffer = new byte[1024];
                    int bytes;

                    do
                    {
                        bytes=inStream.read(buffer);
                        if(bytes!=-1)
                        {
                            msgBuf.addToBuffer(Arrays.copyOfRange(buffer,0, bytes));
                            while(msgBuf.hasMessage())
                            {
                                msg = msgBuf.getMessage();
                            }
                            IRC_processMessage(msg);
                        }
                    }while(bytes!=-1);

                }
                catch(Exception e)
                {
                    System.out.print(e);
                }
            }while(true);

        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Label AddClassLabel;
    private javax.swing.JTextField AddClassName;
    private javax.swing.JMenuItem AddMenuItem;
    private javax.swing.JMenuItem AddOfflineMenuItem;
    private javax.swing.JMenuItem ChatMenuItem;
    private javax.swing.JPanel ChatTab;
    private javax.swing.JMenuItem ConnectMenuItem;
    private java.awt.Label ContactModLabel;
    private javax.swing.JPanel ContactsTab;
    private javax.swing.JLabel Firstnametext;
    private javax.swing.JPanel InteractionPanel;
    private javax.swing.JLabel LionStudyImage;
    private javax.swing.JLabel LionStudyLoginText;
    private javax.swing.JLabel LionStudySignUpLabel;
    private javax.swing.JTabbedPane LionStudyTabs;
    private javax.swing.JLabel LionStudyText;
    private javax.swing.JButton LoginButton;
    private javax.swing.JPanel LoginTab;
    private javax.swing.JButton LogoutButton;
    private javax.swing.JPanel ModOptions;
    private java.awt.Label ModOptionsLabel;
    private javax.swing.JPanel ModOptionsTab;
    private javax.swing.JLabel PasswordText;
    private javax.swing.JPanel ProfileTab;
    private javax.swing.JPanel ProfileTabCoursePanel;
    private javax.swing.JPanel ProfileTabInfoPanel;
    private javax.swing.JMenuItem RemoveMenuItem;
    private javax.swing.JPanel SearchResultListsPanel;
    private javax.swing.JPanel SearchTab;
    private javax.swing.JButton SignUp;
    private javax.swing.JPanel SignUpPanel;
    private javax.swing.JPanel SignUpTab;
    private javax.swing.JPanel SuperImpTab;
    private javax.swing.JPanel TopPanel;
    private javax.swing.JLabel UsernameText;
    private javax.swing.JTextField accountTypeField;
    private javax.swing.JLabel accountTypeLabel;
    private javax.swing.JButton addCourseModButton;
    private javax.swing.JLabel badgeImg;
    private javax.swing.JPanel badgepanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JList<String> contactsList;
    private javax.swing.JPopupMenu contactsMenu;
    private javax.swing.JButton contactsSearchButton;
    private javax.swing.JPanel contactspanel;
    private javax.swing.JPanel courseListPanel;
    private javax.swing.JList<String> courseListProfile;
    private javax.swing.JLabel courseListText;
    private javax.swing.JComboBox<String> coursesComboBox;
    private javax.swing.JPanel coursespanel;
    private javax.swing.JTextField firstNameField;
    private javax.swing.JLabel firstNameLabel;
    private javax.swing.JTextField firstnamefield;
    private javax.swing.JTextArea incomeChatArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JButton joinClassButton;
    private javax.swing.JTextField lastNameField;
    private javax.swing.JLabel lastNameLabel;
    private javax.swing.JTextField lastnamefield;
    private javax.swing.JLabel lastnametext;
    private javax.swing.JTextField lnameFilterField;
    private javax.swing.JPanel lowerpanel;
    private javax.swing.JTextField messageField;
    private javax.swing.JList<String> modsJList;
    private javax.swing.JList<String> offlineJList;
    private javax.swing.JPopupMenu offlineUserMenu;
    private javax.swing.JLabel offlinelabel;
    private javax.swing.JList<String> onlineJList;
    private javax.swing.JPopupMenu onlineUserMenu;
    private javax.swing.JLabel onlinelabel;
    private javax.swing.JPasswordField passwordfield;
    private javax.swing.JPasswordField passwordfieldsignup;
    private javax.swing.JPasswordField passwordfieldsignupreenter;
    private javax.swing.JLabel pennstatelogolabel;
    private javax.swing.JRadioButton professorRB;
    private javax.swing.JPanel profilepanel;
    private java.awt.Label quicklinkslabel;
    private javax.swing.JButton removeCourseButton;
    private javax.swing.JRadioButton studentRB;
    private javax.swing.JButton submitButton;
    private javax.swing.JRadioButton tutorRB;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernamefield;
    private javax.swing.JTextField usernamefieldsignup;
    private javax.swing.JLabel whoareyoulabel;
    // End of variables declaration//GEN-END:variables
}

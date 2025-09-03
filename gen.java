import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class gen {
    JFrame frame;
    JLayeredPane layers;                 // stack menu/game for cross-fade
    FadingPanel menuPanel, gamePanel;
    JButton startButton, exitButton;
    weapon w = new weapon();
    int M = 30, H = 100, S = 50;
    JButton slot4a;
    inden den = new inden();            // your class
    JTextPane mainArea = new JTextPane();
    boolean comp = true;
    int ehealth;
    JPanel lp;
    JPanel top = new JPanel(new GridLayout(2, 1));
    JButton backButton = new JButton("Back to Menu");
    private JPanel cards;        // will hold gameCards
    private JPanel roomcards;
    private JButton backGame;    // single instance, not shadowed


    public static void main(String[] args) {
        SwingUtilities.invokeLater(gen::new);

    }
    public int eHealth(int ehealth){
        return ehealth;
    }

    public gen() {
        frame = new JFrame("RB Quest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Layered pane to overlap menu and game for cross-fade
        layers = new JLayeredPane();
        layers.setPreferredSize(new Dimension(800, 600));

        // Build panels
        menuPanel = buildMenuPanel();
        gamePanel = createGamePanel();
        lp = loadingPanel();
        // Ensure both panels fill the window
        lp.setBounds(0, 0, 800, 600);
        menuPanel.setBounds(0, 0, 800, 600);
        gamePanel.setBounds(0, 0, 800, 600);

        // Initial visibility
        menuPanel.setAlpha(1f);
        menuPanel.setVisible(true);
        gamePanel.setAlpha(0f);
        gamePanel.setVisible(false);
        ((FadingPanel) lp).setAlpha(0f);
        lp.setVisible(false);

        // Add to layers (same layer; we animate alpha)
        layers.add(menuPanel, JLayeredPane.DEFAULT_LAYER);
        layers.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layers.add(lp, JLayeredPane.DEFAULT_LAYER);

        frame.setContentPane(layers);
        frame.setVisible(true);

        // Optional: periodic check for room completion controls slot4a visibility
        Timer checkTimer = new Timer(1000, e -> {
            if (slot4a != null) {
                slot4a.setVisible(RoomY());
            }
        });
        checkTimer.start();
    }

    // ---------- BUILD MENU ----------
    private FadingPanel buildMenuPanel() {
        FadingPanel panel = new FadingPanel(new BorderLayout());
        panel.setBackground(Color.black);

        JLabel titleLabel = new JLabel("ADVENTURE GAME", SwingConstants.CENTER);
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 48));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(175, 100, 100, 100));
        // Keep label non-opaque; let the panel's black show behind it.
        titleLabel.setOpaque(false);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 15));
        buttonPanel.setBackground(Color.black);

        startButton = createMenuButton("START");
        exitButton  = createMenuButton("EXIT");
        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(Color.black);
        wrapper.add(buttonPanel);
        panel.add(wrapper, BorderLayout.CENTER);

        // Actions
        startButton.addActionListener(e -> crossFade(gamePanel, menuPanel));
        exitButton.addActionListener(e -> System.exit(0));

        return panel;
    }
    // __________________________________________________________________________________________________________________________________
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.black);
        btn.setForeground(Color.white);
        btn.setFont(new Font("Arial", Font.PLAIN, 48));
        btn.setPreferredSize(new Dimension(400, 100));
        btn.setFocusPainted(false);
        return btn;
    }

    // ---------- CROSS-FADE ----------
    private void crossFade(FadingPanel show, FadingPanel hide) {
        // Put both visible and animate both alphas together
        show.setAlpha(0f);
        show.setVisible(true);

        hide.setAlpha(1f);
        hide.setVisible(true);

        final int delayMs = 16;   // ~60 FPS
        final float step  = 0.06f; // duration ~ 16/0.06*1000 ≈ 266ms; adjust to taste

        Timer t = new Timer(delayMs, null);
        t.addActionListener(new ActionListener() {
            float a = 0f;
            @Override public void actionPerformed(ActionEvent e) {
                a = Math.min(1f, a + step);
                show.setAlpha(a);
                hide.setAlpha(1f - a);
                if (a >= 1f) {
                    t.stop();
                    hide.setVisible(false);
                    hide.setAlpha(1f);
                    show.setAlpha(1f);
                }
            }
        });
        t.start();
    }

    // ---------- GAME PANEL ----------
    public FadingPanel createGamePanel() {
        FadingPanel panel = new FadingPanel(new BorderLayout());
        panel.setBackground(Color.black);

        // Top: stats + centered message
        JPanel stats = new JPanel(new GridLayout(1, 3));
        stats.setBackground(Color.black);
        stats.setBorder(BorderFactory.createEmptyBorder(25, 25, 0, 25));

        stats.add(createStatLabel("Health: " + H, JLabel.LEFT));
        stats.add(createStatLabel("Strength: " + S, JLabel.CENTER));
        stats.add(createStatLabel("Mana: " + M, JLabel.RIGHT));

        // Replace mainArea direct add with card layout
        CardLayout roomLayout = new CardLayout();
        roomcards = new JPanel(roomLayout);
        roomcards.setBackground(Color.BLACK);
        roomcards.setOpaque(false);
        roomcards.setForeground(Color.white);
        roomcards.setBackground(Color.black);

        mainArea.setText("Starting Room : Please click Next Room in the Attack tab to continue");
        mainArea.setEditable(false);
        mainArea.setOpaque(false);
        mainArea.setForeground(Color.white);
        mainArea.setFont(new Font("Arial", Font.BOLD, 28));
        mainArea.setBorder(BorderFactory.createEmptyBorder(15, 25, 25, 25));

        StyledDocument doc = mainArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        JScrollPane scroll = new JScrollPane(mainArea);
        scroll.setBorder(null); // optional, cleaner look
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);  // <-- important, fixes the grey
        roomcards.add(scroll, "MainArea");

        top.setBackground(Color.BLACK);
        top.add(stats);
        top.add(roomcards);
        panel.add(top, BorderLayout.NORTH);

        // Middle: internal cards (Attack / Items / Spells)
        CardLayout gameLayout = new CardLayout();
        cards = new JPanel(gameLayout);
        cards.setBackground(Color.BLACK);

        JPanel actionWrapper = createActionPanel(gameLayout, cards);
        JPanel attackPanel   = createAttackPanel();
        JPanel itemPanel     = createSimplePanel("Items");
        JPanel spellPanel    = createSimplePanel("Spells");

        cards.add(actionWrapper, "MainActions");
        cards.add(attackPanel,   "Attack");
        cards.add(itemPanel,     "Items");
        cards.add(spellPanel,    "Spells");
        panel.add(cards, BorderLayout.CENTER);

                // Bottom: back button (fixed height bar)
        backButton.setFont(new Font("Arial", Font.PLAIN, 20));
        backButton.setBackground(Color.black);
        backButton.setForeground(Color.white);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> crossFade(menuPanel, gamePanel));

        backGame = new JButton("Back to Game");
        backGame.setFont(new Font("Arial", Font.PLAIN, 20));
        backGame.setBackground(Color.black);
        backGame.setForeground(Color.white);
        backGame.setFocusPainted(false);
        backGame.setVisible(false);
        backGame.addActionListener(e -> {
        CardLayout layout = (CardLayout) cards.getLayout();
        layout.show(cards, "MainActions");
        backButton.setVisible(true);
        backGame.setVisible(false);
    });

        JPanel backWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        backWrapper.setBackground(Color.black);
        backWrapper.add(backButton);
        backWrapper.add(backGame);

        // Force fixed height so it won't get pushed off screen
        backWrapper.setPreferredSize(new Dimension(800, 70));

        panel.add(backWrapper, BorderLayout.SOUTH);


        return panel;
    }
//________________________________________________________________________________________ IN GAME PANELS
    private JLabel createStatLabel(String text, int align) {
        JLabel l = new JLabel(text, align);
        l.setFont(new Font("Arial", Font.BOLD, 28));
        l.setForeground(Color.white);
        return l;
    }

    private JPanel createActionPanel(CardLayout layout, JPanel cards) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.black);

        Dimension size = new Dimension(400, 80);
        JButton attackBtn = createGameButton("Attack", size);
        JButton itemBtn   = createGameButton("Items",  size);
        JButton spellBtn  = createGameButton("Spells", size);
//_____________________________________________________________________________________________________________IN GAME PANEL ACTIONS
        attackBtn.addActionListener(e -> {
            layout.show(cards, "Attack");
            backButton.setVisible(false);
            backGame.setVisible(true);
    });
        itemBtn.addActionListener(e -> {
            layout.show(cards, "Items");
            backButton.setVisible(false);
            backGame.setVisible(true);
    });
        spellBtn.addActionListener(e ->{ 
            layout.show(cards, "Spells");
            backButton.setVisible(false);
            backGame.setVisible(true);
    });

        panel.add(attackBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(itemBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(spellBtn);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(Color.black);
        wrapper.add(panel);
        return wrapper;
    }

    private JButton createGameButton(String text, Dimension size) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.PLAIN, 24));
        b.setBackground(Color.black);
        b.setForeground(Color.white);
        b.setFocusPainted(false);
        b.setPreferredSize(size);
        b.setMaximumSize(size);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }

    private JPanel createAttackPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder("Attack"));

        JButton slot1a = new JButton("Punch");
        slot1a.addActionListener(e ->{
            
            ehealth = eHealth(ehealth-w.damgamount(10));
            frame.repaint();

        });
        JButton slot2a = new JButton("Unknown");
        JButton slot3a = new JButton("Unknown");
        slot4a = new JButton("Next Room");

        for (JButton b : new JButton[]{slot1a, slot2a, slot3a}) {
            b.setBackground(Color.black);
            b.setForeground(Color.white);
            b.setFocusPainted(false);
            panel.add(b);
        }
        slot4a.setBackground(Color.white);
        slot4a.setForeground(Color.black);
        slot4a.setFocusPainted(false);
        panel.add(slot4a);

        slot4a.addActionListener(e -> {
            if (comp) roomIn();
        });

        return panel;
    }

    private JPanel createSimplePanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        for (int i = 0; i < 3; i++) {
            JButton b = new JButton("Unknown");
            b.setBackground(Color.black);
            b.setForeground(Color.white);
            b.setFocusPainted(false);
            panel.add(b);
        }
        return panel;
    }
    public FadingPanel loadingPanel(){
         FadingPanel loading = new FadingPanel( new GridBagLayout());
        JLabel load = new JLabel("Loading...");
        load.setBounds(200,200,300,300);
        loading.add(load);
        loading.setBackground(Color.black);
        return loading;

    }

    // ---------- GAME LOGIC HOOKS ----------
    private boolean RoomY() {
        comp = true; // your logic here
        return comp;
    }
    private void makeRoomA(){
    JPanel roomA = new JPanel(new GridBagLayout()); 
    roomA.setBackground(Color.BLACK);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = GridBagConstraints.RELATIVE;
    gbc.gridy = 0; // stack vertically
    gbc.insets = new Insets(10, 10, 10, 10); // spacing
    gbc.anchor = GridBagConstraints.CENTER;
    ehealth = eHealth(50);
    JLabel text1 = new JLabel("Health : " + ehealth);
    JLabel text2 = new JLabel("Enemy: Goblin ");
    JLabel text3 = new JLabel("Loot: Sword ");

    for (JLabel label : new JLabel[]{text1, text2, text3}) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        roomA.add(label, gbc);
    }

    // Add panel to roomcards
    roomcards.add(roomA, "RoomA");

    // Switch to it
    CardLayout layout = (CardLayout) roomcards.getLayout();
    layout.show(roomcards, "RoomA");
    }

    private void roomIn() {
        rooms room = den.getRandomrooms(); // use your inden instance
        String roomName = room.getName();    // get the actual string

        switch (roomName) {
            case "chestroom": mainArea.setText("ChestRoom!"); 
            comp = true;  
             
            
            break;


            case "enemy1":    
            makeRoomA();
            crossFade( gamePanel,(FadingPanel) lp);  
            comp = false; 
             
            break;

            case "enemy2":    
            crossFade( gamePanel,(FadingPanel) lp);  
            mainArea.setText("enemy2!");    
            comp = false; 
             
            break;

            case "enemy3":    
            crossFade( gamePanel,(FadingPanel) lp);  
            mainArea.setText("enemy3!");    
            comp = false; 
             
            break;

            case "enchant":  
            crossFade( gamePanel,(FadingPanel) lp);  
             mainArea.setText("enchant!");   
            comp = true;  
             
            break;

            default:          mainArea.setText("...");                         break;
        }
    }
}

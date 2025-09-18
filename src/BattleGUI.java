import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class BattleGUI {
    private JFrame frame;
    private JTextArea battleLog;
    private JScrollPane scrollPane;
    private JProgressBar heroHpBar, monsterHpBar;
    private JButton attackButton, healButton, nextFightButton;
    private JLabel killCountLabel;
    private Hero hero;
    private Monster monster;
    private Random random = new Random();
    private int healCount = 5;
    private int monstersKilled = 0;

    public BattleGUI() {
        // Ask player if they want custom or default stats
        int choice = JOptionPane.showConfirmDialog(null, "Do you want to customize stats?", "Choose", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.CLOSED_OPTION) {
        // Player closed the dialog → exit
            JOptionPane.showMessageDialog(null, "Goodbye!");
            System.exit(0);
        }

        if (choice == JOptionPane.YES_OPTION) {
            hero = createCustomHero();
            monster = createCustomMonster();
            if (hero == null || monster == null) {
            // Player canceled custom input → use default
                JOptionPane.showMessageDialog(null, "You canceled custom stats. Using default stats.");
                hero = new Hero("Hero", 100, 20);
                monster = new Monster(80, 15);
            }
        } else {
            // Player chose default stats
            hero = new Hero("Hero", 100, 20);
            monster = new Monster(80, 15);
        }

        setupGUI();
    }

    private Hero createCustomHero() {
        while (true) {
            try {
                String name = JOptionPane.showInputDialog("Enter hero name:");
                if (name == null) return null; // player pressed cancel

                String hpStr = JOptionPane.showInputDialog("Enter hero HP (>0):");
                if (hpStr == null) return null;

                String attackStr = JOptionPane.showInputDialog("Enter hero Attack (>0):");
                if (attackStr == null) return null;

                int hp = Integer.parseInt(hpStr);
                int attack = Integer.parseInt(attackStr);
                if (hp <= 0 || attack <= 0) throw new NumberFormatException();

                return new Hero(name, hp, attack);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter numbers greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Monster createCustomMonster() {
        while (true) {
            try {
                String hpStr = JOptionPane.showInputDialog("Enter monster HP (>0):");
                if (hpStr == null) return null;

                String attackStr = JOptionPane.showInputDialog("Enter monster Attack (>0):");
                if (attackStr == null) return null;

                int hp = Integer.parseInt(hpStr);
                int attack = Integer.parseInt(attackStr);
                if (hp <= 0 || attack <= 0) throw new NumberFormatException();

                return new Monster(hp, attack);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter numbers greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void setupGUI() {
        frame = new JFrame("Mini RPG Battle");
        frame.setSize(550, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Kill count label outside text box
        killCountLabel = new JLabel("Monsters killed: 0");
        killCountLabel.setBounds(50, 20, 200, 30);
        frame.add(killCountLabel);

        // HP Bars
        heroHpBar = new JProgressBar(0, hero.getHp());
        heroHpBar.setValue(hero.getHp());
        heroHpBar.setStringPainted(true);
        heroHpBar.setString(hero.getName() + " HP: " + hero.getHp());
        heroHpBar.setBounds(50, 60, 380, 30);
        frame.add(heroHpBar);

        monsterHpBar = new JProgressBar(0, monster.getHp());
        monsterHpBar.setValue(monster.getHp());
        monsterHpBar.setStringPainted(true);
        monsterHpBar.setString(monster.getName() + " HP: " + monster.getHp());
        monsterHpBar.setBounds(50, 100, 380, 30);
        frame.add(monsterHpBar);

        // Battle log
        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        scrollPane = new JScrollPane(battleLog);
        scrollPane.setBounds(50, 140, 450, 250);
        frame.add(scrollPane);

        // Buttons
        attackButton = new JButton("Attack");
        attackButton.setBounds(50, 410, 100, 30);
        frame.add(attackButton);

        healButton = new JButton("Heal (" + healCount + ")");
        healButton.setBounds(180, 410, 100, 30);
        frame.add(healButton);

        nextFightButton = new JButton("Next Fight");
        nextFightButton.setBounds(310, 410, 120, 30);
        nextFightButton.setEnabled(false);
        frame.add(nextFightButton);

        // Button actions
        attackButton.addActionListener(e -> heroAttack());
        healButton.addActionListener(e -> heroHeal());
        nextFightButton.addActionListener(e -> nextFight());

        frame.setVisible(true);
    }
    
    private int getRandomDamage(int baseAttack) {
        int min = Math.max(1, baseAttack - 5); // minimum 1
        int max = Math.max(min, baseAttack + 5); // ensure max >= min
        return random.nextInt(max - min + 1) + min;
    }

    private void heroAttack() {
        if (!hero.isAlive() || !monster.isAlive()) return;

        int damageToMonster = getRandomDamage(hero.getAttack());
        monster.takeDamage(damageToMonster);

        int damageToHero = getRandomDamage(monster.getAttack());
        hero.takeDamage(damageToHero);

        updateHpBars();
        battleLog.append(hero.getName() + " deals " + damageToMonster + " damage! Monster current HP: " + monster.getHp() + "\n");
        battleLog.append(monster.getName() + " deals " + damageToHero + " damage! " + hero.getName() + " current HP: " + hero.getHp() + "\n\n");
        battleLog.setCaretPosition(battleLog.getDocument().getLength());

        checkBattleOutcome();
    }

    private void heroHeal() {
        if (!hero.isAlive() || !monster.isAlive() || healCount <= 0) return;

        int healAmount = 20;
        int actualHeal = Math.min(healAmount, hero.getMaxHp() - hero.getHp()); // cannot exceed maxHp
        hero.takeDamage(-actualHeal); 
        healCount--;
        healButton.setText("Heal (" + healCount + ")");
        battleLog.append(hero.getName() + " heals " + actualHeal + " HP! Current HP: " + hero.getHp() + "\n\n");

        updateHpBars();
        battleLog.setCaretPosition(battleLog.getDocument().getLength());

    // Monster attacks after heal
        int damageToHero = getRandomDamage(monster.getAttack());
        hero.takeDamage(damageToHero);
        battleLog.append(monster.getName() + " attacks after heal! " + hero.getName() + " takes " + damageToHero + " damage! Current HP: " + hero.getHp() + "\n\n");
        updateHpBars();
        battleLog.setCaretPosition(battleLog.getDocument().getLength());

        checkBattleOutcome();
    }


    private void nextFight() {
    // Always create a new monster with BASE stats, not current HP
        int newHp = 80;     // default or you can scale
        int newAttack = 15; // default or scale
        monster = new Monster(newHp, newAttack);

    // Reset monster HP bar
        monsterHpBar.setMaximum(monster.getHp());
        monsterHpBar.setValue(monster.getHp());
        monsterHpBar.setString(monster.getName() + " HP: " + monster.getHp());

    // Enable buttons
        attackButton.setEnabled(true);
        healButton.setEnabled(healCount > 0);
        nextFightButton.setEnabled(false);

        battleLog.append("A new monster appears!\n\n");
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }


    private Monster createNextMonster() {
        // For simplicity, same monster stats; can scale up if desired
        return new Monster(monster.getHp(), monster.getAttack());
    }

    private void updateHpBars() {
        heroHpBar.setValue(hero.getHp());
        heroHpBar.setString(hero.getName() + " HP: " + hero.getHp());

        monsterHpBar.setValue(monster.getHp());
        monsterHpBar.setString(monster.getName() + " HP: " + monster.getHp());
    }

    private void checkBattleOutcome() {
        if (!hero.isAlive() && !monster.isAlive()) {
            battleLog.append("It's a draw!\n\n");
            endBattle(false);
        } else if (!hero.isAlive()) {
            battleLog.append("You lost!\n\n");
            endBattle(false);
        } else if (!monster.isAlive()) {
            monstersKilled++;
            killCountLabel.setText("Monsters killed: " + monstersKilled);
            battleLog.append("You won!\n\n");
            endBattle(true);
        }
    }

    private void endBattle(boolean heroWon) {
        attackButton.setEnabled(false);
        healButton.setEnabled(false);
        nextFightButton.setEnabled(heroWon);
    }
}
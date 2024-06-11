package BP2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;




public class TTT extends JFrame {
    // Namen van de constants in het spel en hun waardes
    private static final char EMPTY = ' ';
    private static final char PLAYER_X = 'X';
    private static final char PLAYER_O = 'O';
    // x begint eerst met spelen
    private static char currentPlayer = PLAYER_X;
    // 3 x 3 bord maken voor het tic tac toe spel (2d array)
    private static char[][] board = new char[3][3];
    // database connectiecommunicatie connectie
    private static Connection connection;
    // huidige gebruiker id initialiseren op -1
    private static int currentUserId = -1;
    private static int currentScoreId = -1;




    // 3 x 3 bord maken waarbij elke cel een knop is
    private JButton[][] buttons = new JButton[3][3];
    // de borden inkleuren met standaardkleuren in van awt
    private static final Color[] COLORS = {
            Color.YELLOW, Color.GREEN, Color.RED,
            Color.ORANGE, Color.MAGENTA,
            Color.BLUE, Color.CYAN,
            Color.PINK, Color.WHITE
    };




    public static void main(String[] args) { // connectie maken met database, bord initialiseren, app starten
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/TicTacToeDB", "root", "Rowane21");
            initializeBoard();
            SwingUtilities.invokeLater(TTT::new);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public TTT() { // opzetten van de user interface van het spel (windownaam, app sluiten wanneer window sluit, windowgrootte)
        setTitle("Boter Melk en Kaas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());


        // maken en bewerken van de knoppen (font naam, grootte etc)
        JPanel gridPanel = new JPanel(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(new Font("Times New Roman", Font.PLAIN, 100));
                buttons[i][j].setBackground(COLORS[i * 3 + j]);
                buttons[i][j].setFocusPainted(false);
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                gridPanel.add(buttons[i][j]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);
        setVisible(true); // hoofdmenu van spel zichtbaar op het scherm

        displayMainMenu(); //noemt deze method om het hoofdmenu van de app te wijzen in een dialoogvernster of ander deel van de app
    }




    private void displayMainMenu() {
        String[] options = {"Account maken", "Inloggen", "Top 10 scores", "Afsluiten"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(this, "Welkom bij Boter Melk en Kaas", "Hoofdmenu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:
                    // gegevens opslaan en account maken
                    createAccount();
                    if (currentUserId != -1) {
                        resetBoard();
                        return; // het spel starten
                    }
                    break;
                case 1:
                    // Inloggen als gegevens juist zijn
                    if (login()) {
                        resetBoard();
                        return; // het spel starten
                    }
                    break;
                case 2:
                    // Top 10 scores
                    viewTopScores();
                    break;
                case 3:
                    // het spel sluiten
                    System.exit(0);
                    break;
                default:
                    // error code als speler iets anders kiest dan de opties hierboven
                    JOptionPane.showMessageDialog(this, "Keus komt niet voor. Wil je het nogmaals proberen?");
            }
        }
    }


// bord resetten (alle cells leegmaken) zodat er een nieuw spel kan beginnen
    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
    }

    // method om te weten welke cell wordt geklikt
    private class ButtonClickListener implements ActionListener {
        int row, col;

        ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override  // zet x of o waar de speler heeft geklikt (als die cell leeg is), daarna schakelt het die cell uit, checkt als iemand heeft gewonnen of als het gelijk is, als het geen van ze is, verandert het naar de evolgende speler
        public void actionPerformed(ActionEvent e) {
            if (board[row][col] == EMPTY) {
                board[row][col] = currentPlayer;
                buttons[row][col].setText(String.valueOf(currentPlayer));
                buttons[row][col].setForeground(Color.BLACK);
                buttons[row][col].setEnabled(false);
                if (isWinner()) {
                    JOptionPane.showMessageDialog(TTT.this, "Player " + currentPlayer + " wins!");
                    updateScore(currentPlayer == PLAYER_X ? "win" : "loss");
                    resetBoard();
                } else if (isBoardFull()) {
                    JOptionPane.showMessageDialog(TTT.this, "The game is a tie!");
                    updateScore("tie");
                    resetBoard();
                } else {
                    switchPlayer();
                }
            }
        }
    }



    //initialiseert het bord door alle cells op empty te zetten
    private static void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    // laat spelers om de beurt speler
    private static void switchPlayer() {
        currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
    }

    // gaat door alle rijen, kolommen en diagonalen om te kijken of er een winnaar is
    private boolean isWinner() {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) ||
                    (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer)) {
                return true;
            }
        }
        return (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) ||
                (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer);
    }

    // checkt of er nog lege cells zijn om te bepalen of het bord vol is
    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    // voor het maken van een account vraagt het de gebruiker naar: naam, geboortedatum en wachtwoord. Deze info wordt in de database opgeslagen (gebruiker krijgt ook een id)
    private void createAccount() {
        String username = JOptionPane.showInputDialog(this, "Voer je naam in");
        String password = JOptionPane.showInputDialog(this, "Geef een wachtwoord op:");
        String birthdate = JOptionPane.showInputDialog(this, "Voer je geboortedatum in (YYYY-MM-DD):");

        String query = "INSERT INTO users (username, password, birthdate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setDate(3, Date.valueOf(birthdate));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Je hebt succesvol een account aangemaakt");

            // Retrieve the generated user_id
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    currentUserId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        }
    }

    // voor het inloggen vraagt het naar de naam en het wachtwoord van de gebruiker. Als het voorkomt in de database, logt het de gebruiker in
    private boolean login() {
        String username = JOptionPane.showInputDialog(this, "Wat is je naam?");
        String password = JOptionPane.showInputDialog(this, "Wat is je wachtwoord?");

        String query = "SELECT user_id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    currentUserId = rs.getInt("user_id");
                    JOptionPane.showMessageDialog(this, "Login succesvol!");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Naam of wachtwoord is niet juist");
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        }
        return false;
    }

    // geeft de to 10 scores uit de database aan
    private void viewTopScores() {
        String query = "SELECT u.username, s.score FROM scores s JOIN users u ON s.user_id = u.user_id ORDER BY s.score DESC LIMIT 10";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            StringBuilder topScores = new StringBuilder("Top 10 Scores:\n");
            while (rs.next()) {
                topScores.append(rs.getString("username")).append(": ").append(rs.getString("score")).append("\n");
            }
            JOptionPane.showMessageDialog(this, topScores.toString());

        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        }
    }

    // update de database met de naam, user id en resultaat van het spel
    private void updateScore(String result) {
        // huidige speler is altijd player x
        currentPlayer = PLAYER_X;

        // score update bepalen aan de hand van het resultaat
        String scoreUpdate = "";
        if (result.equals("win")) {
            scoreUpdate = (currentPlayer == PLAYER_X) ? "win" : "loss";
        } else if (result.equals("tie")) {
            scoreUpdate = "tie";
        } else {
            scoreUpdate = (currentPlayer == PLAYER_X) ? "loss" : "win";
        }

        // de scores tabel in de database updaten met het resultaat
        String query = "INSERT INTO scores (user_id, score) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUserId);
            stmt.setString(2, scoreUpdate);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Handle any SQL exceptions
            e.printStackTrace();
        }
    }
}

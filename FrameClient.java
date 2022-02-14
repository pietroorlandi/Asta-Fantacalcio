import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Questa classe rappresenta il frame associato al singolo client. Si occupa di gestire e tenere aggiornata tutta l'interfaccia grafica in merito
 * @author pietro
 *
 */
public class FrameClient extends JFrame implements MouseListener {
    private Offer last_offer;
    private JPanel panel_global;
    private JPanel panel_info;
    private JPanel panel_login;
    private JPanel panel_attesa_lobby;
    private JPanel panel_attesa_rilancio;
    private JPanel panel_choose_player;
    private JPanel panel_asta;
    private JComboBox<FantaPlayer> combo_choose_fantaplayer;
    private JLabel lbl_fantaplayer;
    private JLabel lbl_current_price;
    private JButton btn_submit; // btn per fare rilancio
    private JTextField txt_base_asta;
    private JTextField txt_price;
    private JButton btn_invia_base_asta;
    private JPanel cards;
    private JLabel lbl_user;
    private JTextField txt_user;
    private JButton btn_register;
    private JButton btn_lascia;
    private JLabel lbl_username_offerta;
    private JLabel lbl_username;
    private JLabel lbl_attesa_rilancio;
    private JLabel lbl_giocatori_online;
    private JLabel lbl_crediti_rimasti;
    private JLabel lbl_list_fantaplayer;
    private Client client;

    public FrameClient() {
        super("Asta fantacalcio");
        Container pane = this.getContentPane();
        // this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(600, 400);
        Font font = new Font("Calibri", Font.BOLD, 14);

        panel_global = new JPanel();
        panel_info = new JPanel();
        panel_login = new JPanel();
        panel_attesa_lobby = new JPanel();
        panel_attesa_rilancio = new JPanel();
        panel_choose_player = new JPanel();
        panel_asta = new JPanel();

        //components panel_login
        lbl_user = new JLabel("Inserisci username");
        txt_user = new JTextField(10);
        btn_register = new JButton("Vai in lobby");
        btn_register.addMouseListener(this);

        // components panel_info
        lbl_crediti_rimasti = new JLabel();
        lbl_giocatori_online = new JLabel();
        lbl_list_fantaplayer = new JLabel();
        lbl_username = new JLabel();



        lbl_username_offerta = new JLabel();

        // components panel_choose_player
        txt_base_asta = new JTextField(3);
        btn_invia_base_asta = new JButton("Scegli giocatore");
        btn_invia_base_asta.addMouseListener(this);
        combo_choose_fantaplayer = new JComboBox<>();

        // components panel_asta
        lbl_fantaplayer = new JLabel();
        txt_price = new JTextField(3);
        btn_submit = new JButton("Rilancia");
        lbl_current_price = new JLabel();
        btn_submit.addMouseListener(this);
        btn_lascia = new JButton("Lascia");
        btn_lascia.addMouseListener(this);

        // component panel_attesa_rilancio
        lbl_attesa_rilancio = new JLabel();

        lbl_username.setFont(font);
        lbl_giocatori_online.setFont(font);
        lbl_crediti_rimasti.setFont(font);
        lbl_list_fantaplayer.setFont(font);

        lbl_username_offerta.setFont(new Font("Calibri", Font.BOLD, 16));

        panel_info.add(lbl_username);
        panel_info.add(lbl_giocatori_online);
        panel_info.add(lbl_crediti_rimasti);
        panel_info.add(lbl_list_fantaplayer);

        panel_choose_player.setBorder(new EmptyBorder(55, 35, 55, 35));
        panel_choose_player.add(new JLabel("Scegli un giocatore"));
        panel_choose_player.add(combo_choose_fantaplayer);
        panel_choose_player.add(new JLabel("Inserisci base d'asta: "));
        panel_choose_player.add(txt_base_asta);
        panel_choose_player.add(btn_invia_base_asta);
        panel_choose_player.setLayout(new GridLayout(3, 2));

        panel_asta.add(lbl_username_offerta);
        panel_asta.add(new JLabel()); // utilizzata solo per usare spazio
        panel_asta.add(lbl_fantaplayer);
        panel_asta.add(lbl_current_price);
        panel_asta.add(txt_price);
        panel_asta.add(btn_submit);
        panel_asta.add(btn_lascia);
        panel_asta.setLayout(new GridLayout(4, 2));
        panel_asta.setBorder(new EmptyBorder(55, 45, 55, 45));

        panel_login.setLayout(new GridLayout(2, 2));
        panel_login.setBorder(new EmptyBorder(70, 10, 70, 10));
        panel_login.add(lbl_user);
        panel_login.add(txt_user);
        panel_login.add(btn_register);

        JLabel lbl_msg_attesa_riempimento=new JLabel("Attesa per il riempimento della lobby");
        lbl_msg_attesa_riempimento.setFont(new Font("Calibri", Font.BOLD, 18));
        panel_attesa_lobby.add(lbl_msg_attesa_riempimento);
        panel_attesa_lobby.setLayout(new BoxLayout(panel_attesa_lobby, BoxLayout.Y_AXIS));
        panel_info.setLayout(new BoxLayout(panel_info, BoxLayout.Y_AXIS));

        panel_attesa_rilancio.add(lbl_attesa_rilancio);
        panel_info.setBackground(Color.decode("#8c8c8c"));


        cards = new JPanel(new CardLayout());
        cards.add(panel_login, "panel_login");
        cards.add(panel_attesa_lobby, "panel_attesa_lobby");
        cards.add(panel_choose_player, "panel_choose_player");
        cards.add(panel_asta, "panel_asta");
        cards.add(panel_attesa_rilancio, "panel_attesa_rilancio");
        panel_login.setBorder(new EmptyBorder(50, 40, 40, 40));

        panel_global.add(BorderLayout.NORTH, panel_info);
        panel_global.add(BorderLayout.CENTER, cards);
        // pane.add(cards);
        pane.add(BorderLayout.CENTER, panel_global);
        // this.add(panel);
        this.setVisible(true);
    }


    
    /**
     * Quando si iscrive al server e quindi entra in lobby un nuovo user bisogna aggiornare l'interfaccia grafica e mostrare chi è arrivato
     * @param message
     */
    public void update_GUI_new_user(String message) {
        // aggiungo il messaggio scritto
        this.panel_attesa_lobby.add(new JLabel(message));
        this.panel_attesa_lobby.validate();
        this.panel_attesa_lobby.repaint();
    }
    /**
     * Ritorna la lista dei fantaplayer in formato stringa
     * @param lista
     * @return
     */
    public String get_list_fantaplayer_client(List<FantaPlayer> lista) {
        String str_list = "";
        for (FantaPlayer player : lista) {
            str_list += player.get_name() + " ";
        }
        str_list += '\n';
        return str_list;
    }
    /**
     * Quando si vince l'asta per un calciatore aggiorna l'interfaccia grafica per mostrare i crediti e la lista dei calciatori presi aggiornati
     * @param lista
     * @throws RemoteException
     */
    public void update_GUI_vinta_asta(List<FantaPlayer> lista) throws RemoteException {
        System.out.println("Metodo frame_client_vinta_asta");
        String str_list = get_list_fantaplayer_client(lista);
        System.out.println("Lista "+str_list);
        this.lbl_list_fantaplayer.setText("Lista fantaplayer per " + this.client.get_name() + ": " + str_list);
        String str_crediti_rimasti = "Crediti rimasti:" + this.client.get_crediti();
        this.lbl_crediti_rimasti.setText(str_crediti_rimasti);
        if (this.client.is_squadra_completata()){
            panel_attesa_rilancio.add(new JLabel("Hai finito la squadra!"));
        }
    }
    /**
     * Ritorna il valore intero di una stringa passata in ingresso. Nel caso non sia convertibile in intero ritorna -1. Utilizzata per capire la validità di un'offerta presa da interfaccia
     * @param text
     * @return
     */
    public Integer integer_str(String text) {
        int val;
        try {
            val = Integer.parseInt(text);
            return val;
        } catch (NumberFormatException e) {
            return -1; // -1 valore sentinella per dire chè non è Integer
        }
    }
    /**
     * Aggiorna interfaccia grafica per consentire al client di scegliere il calciatore per quel turno
     * @param listone_svincolati
     */
    public void update_GUI_choose_player(List<FantaPlayer> listone_svincolati) {
        this.combo_choose_fantaplayer.removeAllItems();
        // aggiungo il messaggio scritto
        for (FantaPlayer fantaPlayer : listone_svincolati) {
            this.combo_choose_fantaplayer.addItem(fantaPlayer);
        }
        CardLayout cardLayout = (CardLayout) this.cards.getLayout();
        cardLayout.show(cards, "panel_choose_player");
    }

    /**
     * Aggiorna interfaccia grafica per mostrare a utente la nuova offerta ricevuta in ingresso 
     * @param o
     */
    public void update_GUI_asta(Offer o) {
        this.lbl_fantaplayer.setText(o.get_fantaplayer().get_name());
        this.lbl_current_price.setText(String.valueOf(o.get_price()));
        this.lbl_username_offerta.setText("Nuova offerta di " + o.get_username());
        this.last_offer=o;
        CardLayout cardLayout = (CardLayout) this.cards.getLayout();
        cardLayout.show(cards, "panel_asta");
    }
    /**
     * Aggiorna interfaccia grafica per far mostrare all'utente un messaggio per comunicargli che è iniziato un nuovo turno 
     */
    public void update_GUI_nuovo_turno(){
        this.lbl_attesa_rilancio.setText("Iniziato un nuovo turno!");
        CardLayout cardLayout = (CardLayout) this.cards.getLayout();
        cardLayout.show(cards, "panel_attesa_rilancio");
    }

    /**
     * Aggionra interfaccia in modo che utente capisce che l'asta è finita per tutti, ovvero che tutti i client hanno completato la squadra (e il client si potrà disconnettere)
     */
    public void update_GUI_asta_finita(){
        this.lbl_attesa_rilancio.setText("ASTA FINITA PER TUTTI! E' possibile disconettersi e chiudere.");
        CardLayout cardLayout = (CardLayout) this.cards.getLayout();
        cardLayout.show(cards, "panel_attesa_rilancio");
    }


    @Override
    public void mouseClicked(MouseEvent e1) {
        Object o = e1.getSource();
        if (o.equals(btn_register)) {
            String user_name = txt_user.getText(); // check se la sequenza è non nulla
            try {
                // initialization of the client associated to this frame client
                if (user_name.equals("")) { // capisco errore subito senza dover contattare il server
                    JOptionPane.showMessageDialog(null, "Inserisci un username valido!", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    this.client = new Client(this, user_name);
                    if (this.client.start_client()) { // non c'è stato nessun problema nella creazione del client (non è
                                                      // duplicato)
                        lbl_username.setText("Username: " + user_name);
                        this.lbl_list_fantaplayer.setText("Lista fantaplayer: ");
                        String str_crediti_rimasti = "Crediti rimasti:" + this.client.get_crediti();
                        this.lbl_crediti_rimasti.setText(str_crediti_rimasti);
                        panel_info.setBorder(new EmptyBorder(10, 50, 10, 50));
                        CardLayout cardLayout = (CardLayout) this.cards.getLayout();
                        cardLayout.show(cards, "panel_attesa_lobby");
                    } else {
                        JOptionPane.showMessageDialog(null, "Username già presente! Inseriscine un altro", "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
        } else if (o.equals(btn_invia_base_asta)) { // check che il valore sia valido (integer non negativo e entro 100
                                                    // e sotto i crediti max)
            String str_base_asta = txt_base_asta.getText();
            try {
                Integer base_asta = integer_str(str_base_asta);
                FantaPlayer p = (FantaPlayer) combo_choose_fantaplayer.getSelectedItem();
                char ruolo_player = p.get_ruolo();
                if (base_asta != -1 && this.client.offer_is_valid(base_asta, 0, ruolo_player)) {
                    //String player_str = String.valueOf(combo_choose_fantaplayer.getSelectedItem());
                    this.lbl_attesa_rilancio.setText("Hai chiamato il giocatore! Aspetta la mossa degli altri giocatori");
                    CardLayout cardLayout = (CardLayout) this.cards.getLayout();
                    cardLayout.show(cards, "panel_attesa_rilancio");                                                                          
                    InterfaceClient client_successor = this.client.get_successor_client(); // sicuramente avrò successore quando sarò qui perché la schermata compare solo quando la lobby è piena
                    Offer offer = new Offer(this.client.get_name(), p, base_asta);
                    this.last_offer=offer;
                    this.client.set_last_offer(offer);
                    client_successor.rilancio(offer);
                } else {
                    JOptionPane.showMessageDialog(null, "Inserisci un prezzo valido!", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (o.equals(btn_submit)) {
            Integer price_precedent = Integer.parseInt(lbl_current_price.getText());
            Integer price_offered = integer_str(txt_price.getText()); // if -1 (inserito valore non valido)
            if (price_offered == -1) {
                JOptionPane.showMessageDialog(null, "Inserisci un prezzo valido!", "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    FantaPlayer p = this.last_offer.get_fantaplayer();
                    Boolean is_valid = this.client.offer_is_valid(price_offered, price_precedent, p.get_ruolo());
                    if (!is_valid) {
                        JOptionPane.showMessageDialog(null,
                                "Non hai inserito un prezzo valido! (controlla che ti bastino i crediti)", "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        InterfaceClient client_successor = this.client.get_successor_client(); // sicuramente avrò successore quando sarò qui perché la schermata compare solo quando la lobby è piena
                        Offer offer = new Offer(this.client.get_name(), p, price_offered );
                        this.lbl_attesa_rilancio.setText("Hai rilanciato il giocatore! Aspetta la mossa degli altri giocatori");
                        CardLayout cardLayout = (CardLayout) this.cards.getLayout();
                        cardLayout.show(cards, "panel_attesa_rilancio");
                        this.last_offer=offer;
                        this.client.set_last_offer(offer);
                        client_successor.rilancio(offer);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (o.equals(btn_lascia)) {
            String player_str = lbl_fantaplayer.getText(); // aggiungi controllo valore nullo
            System.out.println(player_str+" ha lasciato - offerta vincente per ora: "+this.last_offer.get_csv_offer());
            try {
                this.client.set_partecipate_asta(false);
                this.lbl_attesa_rilancio.setText("Hai lasciato il giocatore! Aspetta un nuovo turno");
                CardLayout cardLayout = (CardLayout) this.cards.getLayout();
                cardLayout.show(cards, "panel_attesa_rilancio");
                InterfaceClient client_successor = this.client.get_successor_client(); // sicuramente avrò successore quando sarò qui perché la schermata compare solo quando la lobby è piena
                client_successor.rilancio(this.last_offer);
            } catch (RemoteException e) {
                System.out.println("Errrore qua");
                e.printStackTrace();
            }
        }

    }
 
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}

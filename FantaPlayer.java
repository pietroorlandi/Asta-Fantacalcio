import java.io.Serializable;

/**
 * Questa classe rappresenta il singolo fantaplayer/calciatore. Questi fantaplayer saranno utilizzati perchè verranno "comprati" dai singoli client.
 * Ogni fantaplayer è rappresentato dagli attributi name, ruolo e club
 * @author pietro
 *
 */
public class FantaPlayer implements Serializable{
    private String name;
    private String club;
    private char ruolo;

    public FantaPlayer(String name, String club, char ruolo){
        this.name=name;
        this.club=club;
        this.ruolo=ruolo;
    }

    /**
     * Ritorna la stringa del fantaplayer 
     * @return
     */
    public String get_fantaplayer_str(){
        return this.name + " - club: "+this.club+" ruolo: "+this.ruolo; 
        // return this.name + " - club: "+this.club+" ruolo: "+toString(this.ruolo); 
    }
    /**
     * Getter del name del fantaplayer
     * @return
     */
    public String get_name(){
        return this.name;
    }
    /**
     * Getter del ruolo del fantaplayer (espresso come un char: {'P','D','C','A'})
     * @return
     */
    public char get_ruolo(){
        return this.ruolo;
    }


    @Override
    public String toString(){
        return this.name+" - "+this.ruolo;
    }

}

import java.io.Serializable;

/**
 * Questa classe rappresenta l'offerta per un singolo calciatore fatta da un client. E' rappresentata dagli attributi username, fantaplayer e price.
 * @author pietro
 *
 */
public class Offer implements Serializable{
    private String username;
    private FantaPlayer fantaplayer;
    private Integer price;

    public Offer(String username, FantaPlayer fantaplayer, int price){
        this.username=username;
        this.fantaplayer=fantaplayer;
        this.price=price;
    }
/** Setter dell'attributo username per settare l'username del client a cui è associata l'offerta
 * @param username 
 */
    public void set_username(String username){
        this.username=username;
    }
    /**
     * Setter dell'attributo fantaplayer dell'offerta
     * @param fantaplayer
     */
    public void set_fantaplayer(FantaPlayer fantaplayer){
        this.fantaplayer=fantaplayer;
    }
    /**
     * Setter dell'attributo price dell'offerta
     * @param price
     */
    public void set_price(int price){
        this.price=price;
    }
    /** Getter dell'attributo username per settare l'username del client a cui è associata l'offerta
    @return
    */
    public String get_username(){
        return this.username;
    }

    /** Getter dell'attributo fantaplayer a cui è associata l'offerta
    @return
    */
    public FantaPlayer get_fantaplayer(){
        return this.fantaplayer;
    }
    /** Getter dell'attributo price per settare il prezzo associata l'offerta
    @return
    */
    public Integer get_price(){
        return this.price;
    }
    /** Metodo di supporto per stampare l'offerta, utilizzato a scopo di debug
    @return ritorna l'offerta in formato csv
    */
    public String get_csv_offer(){
        return this.username+","+this.fantaplayer.get_name()+","+this.price+'\n';
    }
}

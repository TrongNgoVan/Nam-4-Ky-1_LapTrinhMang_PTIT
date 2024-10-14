
package UDP;

import java.io.Serializable;

/**
 *
 * @author Ngọ Văn Trọng
 */
class Product implements Serializable{
    
    private static final long serialVersionUID = 20161107; 
    private String id;
    private String code;
    private String name;
    private int quantity;
    
    public Product(){}
    
    public Product(String id, String code, String name, int quantity){
       this.id = id;
       this.code = code;
       this.name = name;
       this.quantity = quantity;
    }
    
    public String getId(){
        return id;
    }
    
    public String getCode(){
        return code;
    }
    public String getName(){
    
     return name;
    }
    
    public void setName(String Name){
       name = Name;
    }
    
    
    public int getQuantity(){
        return quantity;
    }
    
    public void setQuantity(int SL){
       quantity = SL;
    }
//     ghi đè 1 lớp để in dữ liệu 
    
     @Override
    public String toString() {
        return "Product{id='" + id + "', code='" + code + "', name='" + name + "', quantity=" + quantity + "}";
    }
}

public class Item {

  @SuppressWarnings("unused")
  private int a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z = 0;
  private String str = "";

  public Item(String value) {
    str = value;
  }

  public String getValue() {
    return str;
  }

  public void setValue(String val) {
    str = val;
  }
}

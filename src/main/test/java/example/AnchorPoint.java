package example;


import java.io.Serializable;
 
public class AnchorPoint implements Serializable {
 
    private static final long serialVersionUID = 2L;
 
 
    /**
     * 保持charImgWidth的最大值+1
     */
    private Integer x_iv = 26;
    /**
     * 保持charImgHeight的最大值+1
     */
    private Integer y_iv = 21;
 
    private Integer x;
    private Integer y;
 
    public AnchorPoint(){
        this.x = 0;
        this.y = 0;
    }
 
 
    public AnchorPoint(Integer x, Integer y){
        this.x = x;
        this.y = y;
    }
 
    public Integer getX() {
        return x;
    }
 
 
    public Integer getY() {
        return y;
    }
 
    public void setX(Integer x) {
        this.x = x;
    }
 
    public void setY(Integer y) {
        this.y = y;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnchorPoint that = (AnchorPoint) o;
 
        //当X位置小于偏移量进行判段Y轴的偏移量
        if(this.x+this.x_iv > that.getX() || this.x - this.x_iv < that.getX()){
            //当Y轴的偏移量符合安全距离 判断锚点有效
            if(this.y+this.y_iv < that.getY() || this.y-this.y_iv > that.getY()){
                return false;
            }
        }else{
            //两个对象的X点保持在偏移量之外无需比较Y轴位置，判定锚点有效
            return false;
        }
 
 
        return true;
 
    }
 
    @Override
    public int hashCode() {
        return 1;
    }
 
    @Override
    public String toString() {
        return "DrawXInteger{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
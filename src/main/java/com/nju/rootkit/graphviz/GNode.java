package graphviz;

/**
 * Created by xu on 2017/3/5.
 */
public class GNode {
    private String label;
    private int index;
    private String shape;

    private String gLabel(){
        return "label=\"" + label + "\"";
    }

    private String gShape(){
        return "shape=\"" + shape + "\"";
    }

    private char gStart(){
        return '[';
    }

    private String gEnd(){
        return "];";
    }

    private char gComa(){
        return ',';
    }

    public String toDOTSentence(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(index);
        stringBuilder.append(gStart());
        stringBuilder.append(gLabel());
        stringBuilder.append(gComa());
        stringBuilder.append(gShape());
        stringBuilder.append(gEnd());
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof GNode){
            if (index == ((GNode) obj).getIndex()){
                result = true;
            }
        }else {
            result = false;
        }
        return result;
    }

    public GNode(){
        shape = "box";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

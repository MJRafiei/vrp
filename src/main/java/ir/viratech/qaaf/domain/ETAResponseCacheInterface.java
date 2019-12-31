package ir.viratech.qaaf.domain;

/**
 * ETAResponseCacheInterface
 */
public interface ETAResponseCacheInterface {

    public ETAResponse getETA (Node from, Node to);
    public void addETA (Node from, Node to, ETAResponse eta);
    public void saveTo (String path);
    public void loadFrom (String path);

}
package hu.sztaki.ilab.longneck.process.block;

/**
 * A block with context information to track block uses.
 * Context information is derived from block reference String attributes.
 *
 * @author Csaba Sidló
 */
public interface ContextualBlock extends Block {

   public String getContext() ;
   public void setContext(String context) ;

}

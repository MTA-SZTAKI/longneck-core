package hu.sztaki.ilab.longneck.util;

import hu.sztaki.ilab.longneck.process.block.Block;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Observable that notifies Observers when the Longneck process is over.
 * <br>
 * (See {@link hu.sztaki.ilab.longneck.bootstrap.Bootstrap#run()} )
 * <br>
 * To access this class use {@link ShutdownEventRegister#getShutdownObservable()}
 * <br><br>
 * Blocks can be registerd via {@link #registerBlocks} other objects registration
 * can be done during their initialization, e.g:
 * <br>
 * ShutdownObservable shutdownEvent = ShutdownEventRegister.getShutdownObservable();
 * <br>
 * shutdownEvent.addObserver(this); 
 * 
 * @author Loránd Bendig
 *
 */
public class ShutdownObservable extends Observable {

	ShutdownObservable() {

	}

	/**
	 * Registers blocks for the shutdown notification
	 * <br>
	 * A block will get notified only if it implements the Observer interface
	 * @see java.util.Observer 
	 * @param blocks
	 */
	public void registerBlocks(List<Block> blocks) {
		if (blocks.isEmpty()) {
			return;
		}
		for (Block b : blocks) {
			if (b instanceof Observer) {
				addObserver((Observer) b);
			}
		}
	}

	/**
	 * Sends the shutdown notification to the registered objects
	 */
	public void sendShutdownRequest() {
		setChanged();
		notifyObservers(true);
	}

}
package hu.sztaki.ilab.longneck.util;

/**
 * Singleton class that provides access to the shutdown event notifier
 * @see ShutdownObservable
 * 
 * @author Loránd Bendig Loránd
 *
 */
public enum ShutdownEventRegister {

	INSTANCE;

	private ShutdownObservable blockShutdownObservable = new ShutdownObservable();

	public static ShutdownObservable getShutdownObservable() {
		return INSTANCE.blockShutdownObservable;
	}

}
package sqlclient.core.contracts;

import org.springframework.beans.factory.DisposableBean;

/**
 * @author Neil Attewell
 */
public interface IOutputSink extends DisposableBean {
	void write(String string);
	void writeLine(String string);
	void printInfo(String string);
	void printError(String string);
	
}

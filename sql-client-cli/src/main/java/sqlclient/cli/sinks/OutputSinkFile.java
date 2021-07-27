package sqlclient.cli.sinks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sqlclient.core.util.cli.CommandLine;

/**
 * @author Neil Attewell
 */
@Component
public class OutputSinkFile extends AbstractOutputSink{
	@Autowired private CommandLine commandLine;
	private File file;
	private FileWriter fileWriter;

	public OutputSinkFile() {
	}
	@PostConstruct
	public void init() throws IOException {
		this.file = new File(this.commandLine.getValue("output"));
		this.fileWriter = new FileWriter(this.file);
	}
	@Override
	public void printInfo(String string) {
		System.out.println(string);
	}
	@Override
	public void printError(String string) {
		System.err.println(string);
	}

	@Override
	public void write(String string) {
		try {
			this.fileWriter.write(string);
		} catch (IOException e) {
			printError("Failed to write to file.  Reason:[" + e.getMessage() + "]");
		}
	}
	@Override
	public void writeLine(String string){
		try {
			this.fileWriter.write(string);
			this.fileWriter.write(System.lineSeparator());
		} catch (IOException e) {
			printError("Failed to write to file.  Reason:[" + e.getMessage() + "]");
		}
	}
	public void destroy() throws Exception {
		this.fileWriter.flush();
		this.fileWriter.close();
	}
}

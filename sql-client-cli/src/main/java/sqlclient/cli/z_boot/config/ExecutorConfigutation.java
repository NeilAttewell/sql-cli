package sqlclient.cli.z_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sqlclient.cli.executors.InputExecutorAlias;
import sqlclient.cli.executors.InputExecutorCommit;
import sqlclient.cli.executors.InputExecutorDelimiter;
import sqlclient.cli.executors.InputExecutorExit;
import sqlclient.cli.executors.InputExecutorQuery;
import sqlclient.cli.executors.InputExecutorRollback;
import sqlclient.cli.executors.InputExecutorUse;
import sqlclient.cli.executors.InputExecutorVariable;

/**
 * @author Neil Attewell
 */
@Configuration
public class ExecutorConfigutation {
	@Bean
	public InputExecutorAlias inputExecutorAlias(){
		return new InputExecutorAlias();
	}
	@Bean
	public InputExecutorCommit inputExecutorCommit(){
		return new InputExecutorCommit();
	}
	@Bean
	public InputExecutorDelimiter inputExecutorDelimiter(){
		return new InputExecutorDelimiter();
	}
	@Bean
	public InputExecutorExit inputExecutorExit(){
		return new InputExecutorExit();
	}
	@Bean
	public InputExecutorQuery inputExecutorQuery(){
		return new InputExecutorQuery();
	}
	@Bean
	public InputExecutorRollback inputExecutorRollback(){
		return new InputExecutorRollback();
	}
	@Bean
	public InputExecutorUse inputExecutorUse(){
		return new InputExecutorUse();
	}
	@Bean
	public InputExecutorVariable inputExecutorVariable(){
		return new InputExecutorVariable();
	}
}

package sqlclient.cli.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vavr.Tuple;
import sqlclient.cli.contracts.IQueryResult;

public final class TupleListQueryResult<TupleImp extends Tuple> implements IQueryResult{
	private static final Map<String, Method> REFLECTION = buildMethodReflection();
	private final TupleImp header;
	private final List<TupleImp> values;
	protected int index;
	
	public TupleListQueryResult(TupleImp header, List<TupleImp> values) {
		this.header=header;
		this.values=values;
		this.index=-1;
	}
	@Override
	public int getColumnCount(){
		return this.header.arity();
	}
	@Override
	public String getColumnName(int index){
		var out = getValue(this.header, index);
		if(out == null) {
			return null;
		}
		return "" + out;
	}
	@Override
	public int getColumnDisplaySize(int index){
		return getColumnName(index).length();
	}
	@Override
	public boolean next(){
		this.index++;
		return this.values.size() > this.index;
	}
	@Override
	public Object getObject(int columnIndex){
		return getValue(this.values.get(this.index), columnIndex);
	}
	
	private Object getValue(TupleImp tuple, int index) {
		try {
			return REFLECTION.get("t" + tuple.arity() + "_" + index).invoke(tuple);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return e.getMessage();
		}
	}
	
	
	
	private static Map<String, Method> buildMethodReflection(){
		Map<String, Method> out = new HashMap<>();
		try {
			for(int i = 1 ; i <= 8 ; i++) {
				Class<? extends Tuple> klass = (Class<Tuple>)Class.forName(Tuple.class.getName() + i);
				for(int j = 1 ; j <= i ; j++) {
					out.put("t" + i + "_" + j, klass.getMethod("_" + j));
				}
			}
		}catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return out;
	}
}

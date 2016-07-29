package com.ailk.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.pool.Pool;
import com.ailk.pool.PoolManager;
import com.ailk.util.DateUtil;

public class BaseDao {
	
	private String poolName;
	
	public BaseDao(String poolName){
		this.poolName = poolName;
	}
	
	private static Log log = LogFactory.getLog(BaseDao.class);
	
	@SuppressWarnings("rawtypes")
	public List<Map> queryByPage(String sql, long start, long end){
		String sql_ = "select * from (select rownum row_num, t.*  from (select * from (${sql})  where rownum <=${end}) t) where row_num >${start}";
		sql_ = sql_.replace("${sql}", sql).replace("${start}", start + "").replace("${end}", end + "");
		System.out.println(sql_);
		List<Map> recordList = query(sql_);
		//log.info("execute query complete! return "+ recordList.size() +" records");
		return recordList;
	}

	public List<Map> queryByPageMysql(String sql, long start, long limit){
		String sql_="select * from (select @rownumTmep:=@rownumTmep+1 rownum, p.*  from (select @rownumTmep:=0 as rownumTmep ,t.* from (${sql}) t limit ${end}) p) m where rownum>${start}";
		sql_ = sql_.replace("${sql}", sql).replace("${start}", start + "").replace("${end}", (start+limit) + "");
		List<Map> recordList = query(sql_);
		//去掉rownum 、rownumTmep列
		if(recordList!=null&&recordList.size()>0){
			for(int i=0;i<recordList.size();i++){
				Map map = recordList.get(i);
				map.remove("rownum");
				map.remove("rownumTmep");
				recordList.set(i,map);
			}
		}
		return recordList;
	}
	
	
	@SuppressWarnings("rawtypes")
	public long queryTotalCount(String sql){
		sql = "select count(1) C from ("+ sql +")";
		long totalCount = 0;
		List<Map> result = query(sql);
		totalCount = ((BigDecimal)result.get(0).get("C")).longValue();
		return totalCount;
	}

	@SuppressWarnings("rawtypes")
	public long queryTotalCount2(String sql){
		sql = "select count(1) C from "+ sql +"";
		long totalCount = 0;
		List<Map> result = query(sql);
		totalCount = Long.parseLong(result.get(0).get("C").toString());
		return totalCount;
	}
	
	@SuppressWarnings("rawtypes")
	public long querySequence(String seqName){
		String sql = "select " + seqName + ".nextval as SEQ from dual";
		long seq = -1;
		List<Map> result = query(sql);
		seq = ((BigDecimal)result.get(0).get("SEQ")).longValue();
		return seq;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Map> query(String sql){
		log.info("execute query : " + sql);
		Pool pool = PoolManager.getPool(poolName);
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<Map> recordList = new ArrayList<Map>();
		try{
			conn = (Connection)pool.borrowObject();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData meta = rs.getMetaData();
			while(rs.next()){
				int columnCount = meta.getColumnCount();
				Map record = new LinkedHashMap();
				for(int i = 1; i <= columnCount; i++){
					if(rs.getObject(i) instanceof Date){
						record.put(meta.getColumnName(i), DateUtil.format(rs.getTimestamp(i)));
					}else{
						record.put(meta.getColumnName(i), rs.getObject(i));
					}
				}
				recordList.add(record);
			}
		}catch(Exception e){
			throw new AppRuntimeException(e);
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch(Exception e){
					log.error(e);
				}
			}
			if(stmt != null){
				try{
					stmt.close();
				}catch(Exception e){
					log.error(e);
				}
			}
			try {
				pool.returnObject(conn);
			} catch (Exception e) {
				log.error(e);
			}
		}
		log.debug("execute query complete! return "+ recordList.size() +" records");
		return recordList;
	}
	
	
	public void executeUpdate(String sql, Object[] params){
		Pool pool = PoolManager.getPool(poolName);
		Connection conn = null;
		PreparedStatement  ps = null;
		try{
			conn = (Connection) pool.borrowObject();
			ps = conn.prepareStatement(sql);
			if(params != null){
				for(int i = 0; i< params.length; i++){
					ps.setObject(i + 1, params[i]);
				}
			}
			ps.executeUpdate();
		}catch(Exception e){
			throw new AppRuntimeException(e);
		}finally{
			if(ps != null){
				try{
					ps.close();
				}catch(Exception e){
					log.error(e);
				}
			}
			try {
				pool.returnObject(conn);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	public void executeBatchUpdate(String sql, List<Object[]> paramsList){
		Pool pool = PoolManager.getPool(poolName);
		Connection conn = null;
		PreparedStatement  ps = null;
		try{
			conn = (Connection) pool.borrowObject();
			ps = conn.prepareStatement(sql);
			for(int i=0; i < paramsList.size(); i++){
				Object[] params = paramsList.get(i);
				if(params != null){
					for(int j = 0; j < params.length; j++){
						ps.setObject(j + 1, params[j]);
					}
				}
				ps.addBatch();
				if((i != 0 && i % 1024 == 0) || i == paramsList.size() - 1){
					ps.executeBatch();
					ps.clearBatch();
				}	
			}
			
		}catch(Exception e){
			throw new AppRuntimeException(e);
		}finally{
			if(ps != null){
				try{
					ps.close();
				}catch(Exception e){
					log.error(e);
				}
			}
			try {
				pool.returnObject(conn);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	
}

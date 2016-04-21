package com.rongyifu.mms.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.rongyifu.mms.common.Ryt;

/**
 * 分页的助手类
 * @author Administrator
 *
 * @param <E>
 */
public class PaginationHelper<E> {

	@SuppressWarnings("unchecked")
	public CurrentPage<E> fetchPage(final JdbcTemplate jt, final String sqlCountRows, final String sqlFetchRows,
					final Object args[], final int pageNo, final int pageSize, final RowMapper rowMapper) {
		// determine how many rows are available
		final int rowCount = jt.queryForInt(sqlCountRows, args);
		// calculate the number of pages
		int pageCount = rowCount / pageSize;
		if (rowCount > pageSize * pageCount) {
			pageCount++;
		}
		// create the page object
		final CurrentPage<E> page = new CurrentPage<E>();
		page.setPageNumber(pageNo);
		page.setPagesAvailable(pageCount);
		page.setPageSize(pageSize);
		page.setPageTotle(rowCount);
		// fetch a single page of results
		final int startRow = (pageNo - 1) * pageSize;
		jt.query(sqlFetchRows, args, new ResultSetExtractor() {
			@Override
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				final List pageItems = page.getPageItems();
				int currentRow = 0;
				while (rs.next() && currentRow < startRow + pageSize) {
					if (currentRow >= startRow) {
						pageItems.add(rowMapper.mapRow(rs, currentRow));
					}
					currentRow++;
				}
				return page;
			}
		});
		return page;
	}
	
	
	@SuppressWarnings("unchecked")
	public CurrentPage<E> fetchPage2(final JdbcTemplate jt, final String sqlCountRows, final String sqlFetchRows
		, final String sqlAmt,final Object args[], final int pageNo, final int pageSize, final RowMapper rowMapper) {
//		// determine how many rows are available
//		final int rowCount = jt.queryForInt(sqlCountRows, args);
//		// calculate the number of pages
//		int pageCount = rowCount / pageSize;
//		if (rowCount > pageSize * pageCount) {
//			pageCount++;
//		}
//		// create the page object
//		final CurrentPage<E> page = new CurrentPage<E>();
//		page.setPageNumber(pageNo);
//		page.setPagesAvailable(pageCount);
//		page.setPageSize(pageSize);
//		page.setPageTotle(rowCount);
//		//-------------------与上面不同的地方-------------		
//		page.setPageAmtSum(jt.queryForLong(sqlAmt, args));		
//		//-------------------与上面不同的地方-------------
//		// fetch a single page of results
//		final int startRow = (pageNo - 1) * pageSize;
//		jt.query(sqlFetchRows, args, new ResultSetExtractor() {
//			@SuppressWarnings("unchecked")
//			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
//				final List pageItems = page.getPageItems();
//				int currentRow = 0;
//				while (rs.next() && currentRow < startRow + pageSize) {
//					if (currentRow >= startRow) {
//						pageItems.add(rowMapper.mapRow(rs, currentRow));
//					}
//					currentRow++;
//				}
//				return page;
//			}
//		});
//		return page;
		
		CurrentPage<E> page = fetchPage(jt,sqlCountRows,sqlFetchRows,args,pageNo,pageSize,rowMapper);
		if(!Ryt.empty(sqlAmt))
		page.setPageAmtSum(jt.queryForLong(sqlAmt, args));
		return page;
}



@SuppressWarnings("unchecked")
public CurrentPage<E> fetchPage3(final JdbcTemplate jt, final String sqlCountRows, final String sqlFetchRows
	, final String[] sqlAmtSum,final Object args[], final int pageNo, final int pageSize, final RowMapper rowMapper) {
//	// determine how many rows are available
//	final int rowCount = jt.queryForInt(sqlCountRows, args);
//	// calculate the number of pages
//	int pageCount = rowCount / pageSize;
//	if (rowCount > pageSize * pageCount) {
//		pageCount++;
//	}
//	// create the page object
//	final CurrentPage<E> page = new CurrentPage<E>();
//	page.setPageNumber(pageNo);
//	page.setPagesAvailable(pageCount);
//	page.setPageSize(pageSize);
//	page.setPageTotle(rowCount);
//	//-------------------与上面不同的地方-------------
//	if(sqlAmtSum[0]!=null&&!sqlAmtSum[0].equals("")){
//		page.setPageAmtSum(jt.queryForLong(sqlAmtSum[0], args));
//	}
//	if(sqlAmtSum[1]!=null&&!sqlAmtSum[1].equals("")){
//		page.setSysAmtFeeSum(jt.queryForLong(sqlAmtSum[1], args));
//	}
//	//-------------------与上面不同的地方-------------
//	// fetch a single page of results
//	final int startRow = (pageNo - 1) * pageSize;
//	jt.query(sqlFetchRows, args, new ResultSetExtractor() {
//		@SuppressWarnings("unchecked")
//		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
//			final List pageItems = page.getPageItems();
//			int currentRow = 0;
//			while (rs.next() && currentRow < startRow + pageSize) {
//				if (currentRow >= startRow) {
//					pageItems.add(rowMapper.mapRow(rs, currentRow));
//				}
//				currentRow++;
//			}
//			return page;
//		}
//	});
//	return page;
	
	CurrentPage<E> page = fetchPage(jt,sqlCountRows,sqlFetchRows,args,pageNo,pageSize,rowMapper);
	if(!Ryt.empty(sqlAmtSum[0]))
	page.setPageAmtSum(jt.queryForLong(sqlAmtSum[0], args));
	if(!Ryt.empty(sqlAmtSum[1]))
	page.setSysAmtFeeSum(jt.queryForLong(sqlAmtSum[1], args));
	return page;
}
}

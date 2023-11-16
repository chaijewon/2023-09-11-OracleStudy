package com.sist.dao;
import java.util.*;
import java.sql.*;
// 핵심 
// JDBC => DBCP => ORM (MyBatis / JPA)
/*
 *   return selectList(SQL)
 */
public class MovieDAO {
   private Connection conn;
   private PreparedStatement ps;
   private final String URL="jdbc:oracle:thin:@localhost:1521:xe";
   // 에러 => output => this.conn NULL
   public MovieDAO()
   {
	   try
	   {
		   Class.forName("oracle.jdbc.driver.OracleDriver");
	   }catch(Exception ex) {}
   }
   public void getConnection()
   {
	   try
	   {
		   conn=DriverManager.getConnection(URL,"hr","happy");
	   }catch(Exception ex) {}
   }
   public void disConnection()
   {
	   try
	   {
		   if(ps!=null) ps.close();
		   if(conn!=null) conn.close();
	   }catch(Exception ex) {}
   }
   public List<MovieVO> movieListData(int page)
   {
	   // rownum
	   List<MovieVO> list=new ArrayList<MovieVO>();
	   try
	   {
		   getConnection();
		   int rowSize=12;
		   int start=(rowSize*page)-(rowSize-1);
		   // (page-1)*rowSize
		   int end=rowSize*page;
		   String sql="SELECT mno,title,poster,num "
				     +"FROM (SELECT mno,title,poster,rownum as num "
				     +"FROM (SELECT mno,title,poster "
				     +"FROM movie ORDER BY mno ASC)) "
				     +"WHERE num BETWEEN ? AND ?";
		   ps=conn.prepareStatement(sql);
		   ps.setInt(1, start);
		   ps.setInt(2, end);
		   ResultSet rs=ps.executeQuery();
		   while(rs.next())
		   {
			   MovieVO vo=new MovieVO();
			   vo.setMno(rs.getInt(1));
			   vo.setTitle(rs.getString(2));
			   vo.setPoster(rs.getString(3));
			   list.add(vo);
		   }
		   rs.close();
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   finally
	   {
		   disConnection();
	   }
	   return list;
   }
   // 총페이지 
   public int movieTotalPage()
   {
	   int total=0;
	   try
	   {
		   getConnection();
		   String sql="SELECT CEIL(COUNT(*)/12.0) FROM movie";
		   ps=conn.prepareStatement(sql);
		   ResultSet rs=ps.executeQuery();
		   rs.next();
		   total=rs.getInt(1);
		   rs.close();
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   finally
	   {
		   disConnection();
	   }
	   return total;
   }
   public MovieVO movieDetailData(int mno)
   {
	   MovieVO vo=new MovieVO();
	   try
	   {
		   getConnection();
		   String sql="SELECT mno,title,actor,poster,"
				     +"genre,grade,director "
				     +"FROM movie "
				     +"WHERE mno="+mno;
		   ps=conn.prepareStatement(sql);
		   ResultSet rs=ps.executeQuery();
		   rs.next();
		   vo.setMno(rs.getInt(1));
		   vo.setTitle(rs.getString(2));
		   vo.setActor(rs.getString(3));
		   vo.setPoster(rs.getString(4));
		   vo.setGenre(rs.getString(5));
		   vo.setGrade(rs.getString(6));
		   vo.setDirector(rs.getString(7));
		   rs.close();
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   finally
	   {
		   disConnection();
	   }
	   return vo;
   }
   //  검색 => Ajax => Vue/React(Ajax를 포함)
}

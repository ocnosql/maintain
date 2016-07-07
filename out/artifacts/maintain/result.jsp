<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.ailk.model.ext.ResultBuild,java.util.*" %>
<%
String queryType = request.getParameter("queryType");

Map<String, List> testData = new HashMap<String, List>();
Map<String, String[]> fields = new HashMap<String, String[]>();
Map<String, List> testData2 = new HashMap<String, List>();


fields.put("taskList",new String[]{"主键", "任务类型", "任务执行时间", "任务执行时长", "状态", "详细信息"});
List taskListList = new ArrayList();
taskListList.add(new String[]{"00001","详单条数稽核","2015-12-03 09:30:10","20m","运行中","查询详情"});
taskListList.add(new String[]{"00002","一致性稽核","2015-12-03 09:40:10","30m","成功","查询详情"});
taskListList.add(new String[]{"00003","主键查询","2015-12-03 09:50:10","1s","成功","查询详情"});
taskListList.add(new String[]{"00004","数据修改","2015-12-03 10:10:10","2s","成功","查询详情"});
taskListList.add(new String[]{"00005","数据删除","2015-12-03 10:20:10","3s","失败","查询详情"});
testData2.put("taskList", taskListList);


/*****************************************************/
List rowkeyQueryList = new ArrayList();
fields.put("rowkeyQuery",new String[]{"用户号码", "CALL_TYPE", "START_TIME", "BILL_MONTH"});
rowkeyQueryList.add(new String[]{"18601134210","1","20151201123023","201512"});
rowkeyQueryList.add(new String[]{"17606712167","1","20151202185010","201512"});
testData2.put("rowkeyQuery", rowkeyQueryList);

/*****************************************************/
List tableCountDetailList = new ArrayList();
fields.put("tableCountDetail",new String[]{"日期", "条数"});
tableCountDetailList.add(new String[]{"20151201","2000"});
tableCountDetailList.add(new String[]{"20151202","3000"});
tableCountDetailList.add(new String[]{"总计","5000"});
testData2.put("tableCountDetail", tableCountDetailList);


/*****************************************************/
fields.put("consistencyCheckDetail",new String[]{"日期", "业务类型", "科目", "金额"});
List<String[]> consistencyCheckDetailList = new ArrayList<String[]>();
consistencyCheckDetailList.add(new String[]{"20151201","cs","科目1","10000"});
consistencyCheckDetailList.add(new String[]{"20151201","cs","科目2","20000"});
consistencyCheckDetailList.add(new String[]{"20151201","cs","科目3","30000"});
consistencyCheckDetailList.add(new String[]{"20151201","sms","科目1","20000"});
consistencyCheckDetailList.add(new String[]{"20151201","sms","科目2","10000"});
consistencyCheckDetailList.add(new String[]{"20151201","mms","科目1","30000"});
consistencyCheckDetailList.add(new String[]{"20151201","imsp","科目1","40000"});
consistencyCheckDetailList.add(new String[]{"20151201","imsp","科目2","30000"});
consistencyCheckDetailList.add(new String[]{"20151201","ps","科目1","10000"});
consistencyCheckDetailList.add(new String[]{"20151201","ps","科目2","20000"});
consistencyCheckDetailList.add(new String[]{"20151201","ps","科目3","30000"});

consistencyCheckDetailList.add(new String[]{"20151202","cs","科目1","10000"});
consistencyCheckDetailList.add(new String[]{"20151202","cs","科目2","20000"});
consistencyCheckDetailList.add(new String[]{"20151202","cs","科目3","30000"});
consistencyCheckDetailList.add(new String[]{"20151202","sms","科目1","20000"});
consistencyCheckDetailList.add(new String[]{"20151202","sms","科目2","10000"});
consistencyCheckDetailList.add(new String[]{"20151202","mms","科目1","30000"});
consistencyCheckDetailList.add(new String[]{"20151202","imsp","科目1","40000"});
consistencyCheckDetailList.add(new String[]{"20151202","imsp","科目2","30000"});
consistencyCheckDetailList.add(new String[]{"20151202","ps","科目1","10000"});
consistencyCheckDetailList.add(new String[]{"20151202","ps","科目2","20000"});
consistencyCheckDetailList.add(new String[]{"20151202","ps","科目3","30000"});
testData2.put("consistencyCheckDetail", consistencyCheckDetailList);

/*****************************************************/
List sqlDetailList = new ArrayList();
fields.put("sqlDetail",new String[]{"total_count"});
sqlDetailList.add(new String[]{"1000000000"});
testData2.put("sqlDetail", sqlDetailList);

out.write(ResultBuild.buildJson(fields.get(queryType), testData2.get(queryType)));


/* testData.put("consistencyCheck", consistencyCheckList);
testData.put("rowkeyQuery", rowkeyQueryList);
testData.put("tableCountDetail", tableCountDetailList);

out.write(ResultBuild.buildResult(testData.get(queryType))); */




%>
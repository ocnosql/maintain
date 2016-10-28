package com.ailk.service.impl;

import com.ailk.core.exception.AppRuntimeException;
import com.ailk.dao.BaseDao;
import com.ailk.model.ResultDTO;
import com.ailk.model.ws.User;

import java.util.List;
import java.util.Map;

/**
 * Created by scj on 2016/8/23.
 */
public class UserService {

    public ResultDTO addUser(User user){
        //先查询user是否已经存在
        //
        BaseDao dao = new BaseDao("ocnosql");
        ResultDTO resultDTO = new ResultDTO();
        try {
            String sqlCountStr = "select count(*) C from user where name='"+user.getName()+"'";
            List<Map> result =  dao.query(sqlCountStr);
            long count = Long.parseLong(result.get(0).get("C").toString());
            //如果存在直接返回
            if(count>0){
                resultDTO.setSuccess(false);
                resultDTO.setMessage("用户已存在");
                return resultDTO;
            }

            //如果不存在插入数据库
            String sqlInsertStr = "insert into user(name,passwd,is_enable) values(?,?,?)";
            Object[] paramsInsert = new Object[3];
            paramsInsert[0] = user.getName();
            paramsInsert[1] = user.getPasswd();
            paramsInsert[2] = user.getIs_enable();

            dao.executeUpdate(sqlInsertStr,paramsInsert);

            //查询出新增用户的Id，并返回
            String sqlIdStr = "select id from user where name='"+user.getName()+"'";
            List<Map> resultId =  dao.query(sqlIdStr);
            String idStr = String.valueOf(resultId.get(0).get("id"));

            resultDTO.setSuccess(true);
            resultDTO.setMessage(idStr);
            return resultDTO;
        } catch (Throwable e) {
            resultDTO.setSuccess(false);
            resultDTO.setMessage(e.getMessage());
            //throw new AppRuntimeException(e);
        }
        return resultDTO;
    }

    public ResultDTO deleteUser(User user){
        //不再判断账号是否存在，直接删除
        BaseDao dao = new BaseDao("ocnosql");
        ResultDTO resultDTO = new ResultDTO();
        try{
            String sqlDeleteStr = "delete from user where name =?";
            Object[] paramsDelete = new Object[1];
            paramsDelete[0] = user.getName();
            dao.executeUpdate(sqlDeleteStr,paramsDelete);

            resultDTO.setSuccess(true);
            resultDTO.setMessage("成功");
            return resultDTO;

        }catch (Throwable e){
            resultDTO.setSuccess(false);
            resultDTO.setMessage(e.getMessage());
        }
        return resultDTO;
    }

    public ResultDTO resetUser(User user){
        //不再判断账号是否存在，直接删除
        BaseDao dao = new BaseDao("ocnosql");
        ResultDTO resultDTO = new ResultDTO();
        try{
            String sqlUpdateStr = "update user set passwd = ? where name =?";
            Object[] paramsUpdate = new Object[2];
            paramsUpdate[0] = user.getPasswd();
            paramsUpdate[0] = user.getName();
            dao.executeUpdate(sqlUpdateStr,paramsUpdate);

            //查询出修改用户的Id，并返回
            String sqlIdStr = "select id from user where name='"+user.getName()+"'";
            List<Map> resultId =  dao.query(sqlIdStr);
            String idStr = String.valueOf(resultId.get(0).get("id"));

            resultDTO.setSuccess(true);
            resultDTO.setMessage(idStr);
            return resultDTO;

        }catch (Throwable e){
            resultDTO.setSuccess(false);
            resultDTO.setMessage(e.getMessage());
        }
        return resultDTO;
    }

    public ResultDTO changeStatusUser(User user){
        //不再判断账号是否存在，直接删除
        BaseDao dao = new BaseDao("ocnosql");
        ResultDTO resultDTO = new ResultDTO();
        try{
            String sqlUpdateStr = "update user set is_enable = ? where name =?";
            Object[] paramsUpdate = new Object[2];
            paramsUpdate[0] = user.getIs_enable();
            paramsUpdate[0] = user.getName();
            dao.executeUpdate(sqlUpdateStr,paramsUpdate);

            //查询出修改用户的Id，并返回
            String sqlIdStr = "select id from user where name='"+user.getName()+"'";
            List<Map> resultId =  dao.query(sqlIdStr);
            String idStr = String.valueOf(resultId.get(0).get("id"));

            resultDTO.setSuccess(true);
            resultDTO.setMessage(idStr);
            return resultDTO;

        }catch (Throwable e){
            resultDTO.setSuccess(false);
            resultDTO.setMessage(e.getMessage());
        }
        return resultDTO;
    }

}

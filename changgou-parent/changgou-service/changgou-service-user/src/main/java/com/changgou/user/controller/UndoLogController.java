package com.changgou.user.controller;
import com.changgou.user.pojo.UndoLog;
import com.changgou.user.service.UndoLogService;
import com.github.pagehelper.PageInfo;
import entity.Result;
import entity.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.changgou.core.AbstractCoreController;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/undoLog")
@CrossOrigin
public class UndoLogController extends AbstractCoreController<UndoLog>{

    private UndoLogService  undoLogService;

    @Autowired
    public UndoLogController(UndoLogService  undoLogService) {
        super(undoLogService, UndoLog.class);
        this.undoLogService = undoLogService;
    }
}

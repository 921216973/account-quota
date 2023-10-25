package com.accountquota.facade;

import com.accountquota.vo.req.salesman.AdjustReq;
import com.accountquota.vo.req.salesman.InitReq;
import com.accountquota.vo.resp.salesman.AdjustResp;
import com.accountquota.vo.resp.salesman.InitResp;

public interface SalesmanFacade {

    InitResp init(InitReq entry) ;

    AdjustResp adjust(AdjustReq entry);
}

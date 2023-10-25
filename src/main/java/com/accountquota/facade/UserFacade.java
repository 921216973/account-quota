package com.accountquota.facade;


import com.accountquota.vo.req.user.DeductedReq;
import com.accountquota.vo.req.user.SearchReq;
import com.accountquota.vo.resp.user.DeductedResp;
import com.accountquota.vo.resp.user.SearchResp;

import java.util.List;

public interface UserFacade {

    SearchResp search(SearchReq entry);

    DeductedResp deducted(DeductedReq entry);
}

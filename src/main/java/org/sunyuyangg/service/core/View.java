package org.sunyuyangg.service.core;


import com.ibm.staf.STAFResult;
import org.sunyuyangg.service.core.adapter.ModelAndView;

public interface View {
    void render(ModelAndView model, STAFResult response);
}

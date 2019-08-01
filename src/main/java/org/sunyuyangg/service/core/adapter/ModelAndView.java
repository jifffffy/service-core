package org.sunyuyangg.service.core.adapter;

public class ModelAndView {

    private Object model;
    private int rc;

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    public Object getModel() {
        return model;
    }
}

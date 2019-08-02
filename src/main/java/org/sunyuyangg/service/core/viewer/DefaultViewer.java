package org.sunyuyangg.service.core.viewer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import org.sunyuyangg.service.core.Util;
import org.sunyuyangg.service.core.View;
import org.sunyuyangg.service.core.adapter.ModelAndView;


public class DefaultViewer implements View {


    public DefaultViewer() {

    }

    @Override
    public void render(ModelAndView model, STAFResult response) {
        STAFMarshallingContext context = new STAFMarshallingContext();
        try {
            response.result = Util.objectMapper().writeValueAsString(model.getModel());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.setRootObject(model.getModel());
            response.result = context.marshall();
        }
        response.rc = model.getRc();
    }
}

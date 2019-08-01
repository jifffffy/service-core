package org.sunyuyangg.service.core.viewer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import org.sunyuyangg.service.core.View;
import org.sunyuyangg.service.core.adapter.ModelAndView;


public class DefaultViewer implements View {

    private ObjectMapper objectMapper;

    public DefaultViewer() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public void render(ModelAndView model, STAFResult response) {
        STAFMarshallingContext context = new STAFMarshallingContext();
        try {
            response.result = objectMapper.writeValueAsString(model.getModel());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.setRootObject(model.getModel());
            response.result = context.marshall();
        }
        response.rc = model.getRc();
    }
}

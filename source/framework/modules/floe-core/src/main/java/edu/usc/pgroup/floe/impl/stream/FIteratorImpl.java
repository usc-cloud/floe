package edu.usc.pgroup.floe.impl.stream;

import edu.usc.pgroup.floe.api.exception.LandmarkException;
import edu.usc.pgroup.floe.api.exception.LandmarkPauseException;
import edu.usc.pgroup.floe.api.stream.FIterator;
import edu.usc.pgroup.floe.impl.communication.MessageImpl;
import edu.usc.pgroup.floe.impl.queues.*;
import edu.usc.pgroup.floe.api.util.BitConverter;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FIteratorImpl implements FIterator {
    private static final Logger logger = Logger.getLogger(FIteratorImpl.class.getName());
    private final StreamSourceQueue sourceRouter;
    private boolean pauseLandmark = false;

    public FIteratorImpl(StreamSourceQueue sourceRouter) {
        this.sourceRouter = sourceRouter;
    }

    @Override
    public Object next(int timeout, TimeUnit timeunit) throws LandmarkException, LandmarkPauseException {
        MessageImpl message = null;
        if (pauseLandmark == true) {
            throw new LandmarkPauseException("Request to pause");
        }
        message = (MessageImpl) sourceRouter.getMessage(timeout, timeunit);

        if (message == null) {
            return null;
        }

        message.setCurrentContext();

        if (message.getLandMark() == true) {
            throw new LandmarkException("Landmark at input");
        }
        byte[] bytes = null;
        try {
            bytes = (byte[]) message.getPayload();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Typecast exception", e);
        }
        Object object = BitConverter.getObject(bytes);
        System.out.println("message " + object + " and not landmark");
        return object;
    }

    @Override
    public Object peek() {
        MessageImpl m = null;
        int count = 0;
        while (m == null && count < 5) {
            m = (MessageImpl) sourceRouter.peekMessage();
            if (m != null) {
                m.setCurrentContext();
                return m;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
        }

        return m;
    }

    @Override
    public Object next() throws LandmarkException, LandmarkPauseException {
        Object ret = null;
        while (ret == null) {
            ret = next(5000, TimeUnit.MICROSECONDS);
        }
        return ret;
    }

    public void setPauseLandmark() {
        pauseLandmark = true;
    }
}

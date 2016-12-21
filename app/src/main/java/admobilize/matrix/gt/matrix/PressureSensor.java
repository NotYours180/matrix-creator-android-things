package admobilize.matrix.gt.matrix;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import admobilize.matrix.gt.Config;

/**
 * Created by Antonio Vanegas @hpsaturn on 12/20/16.
 */

public class PressureSensor extends SensorBase {

    private static final String TAG = PressureSensor.class.getSimpleName();
    private static final boolean DEBUG = Config.DEBUG;

    private float altitude;
    private float pressure;
    private float temperature;

    public PressureSensor(Wishbone wb) {
        super(wb);
    }

    public void read (){
        byte[] data = new byte[12];
        wb.SpiRead((short) (kMCUBaseAddress+(kMemoryOffsetPressure >> 1)),data,12);
        this.altitude=ByteBuffer.wrap(data,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.pressure=ByteBuffer.wrap(data,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        this.temperature=ByteBuffer.wrap(data,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    public float getAltitude() {
        return altitude;
    }

    public float getPressure() {
        return pressure;
    }

    public float getTemperature() {
        return temperature;
    }
}

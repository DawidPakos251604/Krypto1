import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SBoxTest {

    @Test
    void testTranslateS_Box() {
        byte b = (byte) 0xaa;
        assertEquals((byte) 0xac, SBox.translateS_Box(b));
    }

    @Test
    void testTranslateInv_S_Box() {
        byte b = (byte) 0xaa;
        assertEquals((byte) 0x62, SBox.translateInv_S_Box(b));
    }
}

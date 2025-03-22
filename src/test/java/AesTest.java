import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AesTest {

    @Test
    public void testShiftRows() {
        byte[] state = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x43, (byte) 0x5a, (byte) 0x31, (byte) 0x37,
                (byte) 0xf6, (byte) 0x30, (byte) 0x98, (byte) 0x07,
                (byte) 0xa8, (byte) 0x8d, (byte) 0xa2, (byte) 0x34
        };

        System.out.println("Test result (normal shiftRows):");
        for (byte b : state) {
            System.out.printf("0x%02x ", b);
        }
        System.out.println();

        // Expected output after applying reversed shift rows
        byte[] expectedState = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x5a, (byte) 0x31, (byte) 0x37, (byte) 0x43,
                (byte) 0x98, (byte) 0x07, (byte) 0xf6, (byte) 0x30,
                (byte) 0x34, (byte) 0xa8, (byte) 0x8d, (byte) 0xa2
        };

        byte[] result = Aes.shiftRows(state);

        System.out.println("Test result (normal shiftRows):");
        for (byte b : result) {
            System.out.printf("0x%02x ", b);
        }
        System.out.println();

        assertArrayEquals(expectedState, result);
    }

    @Test
    public void testShiftRowsReversed() {
        byte[] state = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x43, (byte) 0x5a, (byte) 0x31, (byte) 0x37,
                (byte) 0xf6, (byte) 0x30, (byte) 0x98, (byte) 0x07,
                (byte) 0xa8, (byte) 0x8d, (byte) 0xa2, (byte) 0x34
        };

        // Expected output after applying reversed shift rows
        byte[] expectedState = {
                (byte) 0x32, (byte) 0x88, (byte) 0x31, (byte) 0xe0,
                (byte) 0x37, (byte) 0x43, (byte) 0x5a, (byte) 0x31,
                (byte) 0x98, (byte) 0x07, (byte) 0xf6, (byte) 0x30,
                (byte) 0x8d, (byte) 0xa2, (byte) 0x34, (byte) 0xa8
        };

        byte[] result = Aes.shiftRowsReversed(state);

        assertArrayEquals(expectedState, result);
    }
}

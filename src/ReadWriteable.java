import java.io.File;
import java.io.IOException;

/**
 * @author Brad Huang
 * @date Nov.2, 2016
 * Interface for reading and writing files
 */
public interface ReadWriteable {
	/**
	 * reads hour file
	 * @throws IOException
	 */
	void readHours() throws IOException;

	/**
	 * writes the hour file
	 * @throws IOException
	 */
	void writeHours() throws IOException;
}

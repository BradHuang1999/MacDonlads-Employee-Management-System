import java.io.IOException;

/**
 * ReadWritable.java
 * @author Brad Huang
 * Nov.2, 2016
 * Interface for reading and writing files
 */
public interface ReadWriteable {

	 /**
	 * reads the hour file
	 * @throws IOException when file not found
	 */
	void readHours() throws IOException;

	/**
	 * writes the hour file
	 * @throws IOException when file not found
	 */
	void writeHours() throws IOException;
}

import java.io.File;
import java.io.IOException;

public interface ReadWriteable {
	void readHours() throws IOException;

	void writeHours() throws IOException;
}

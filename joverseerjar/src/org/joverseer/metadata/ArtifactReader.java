package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;

/**
 * Reads starting artifact information
 * 
 * @author Marios Skounakis
 * 
 */
public class ArtifactReader implements MetadataReader {
	String artifactFilename = "arties.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		gm.setArtifacts(loadArtifacts(gm));
	}

	private Container<ArtifactInfo> loadArtifacts(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<ArtifactInfo> artifacts = new Container<ArtifactInfo>();

		try {
			BufferedReader reader = gm.getUTF8ResourceByGame(this.artifactFilename);

			String ln;
			while ((ln = reader.readLine()) != null) {
				try {
					String[] parts = ln.split(";");
					int no = Integer.parseInt(parts[0]);
					String name = parts[1];
					String power1 = parts.length > 3 ? parts[3] : "";
					String bonus = parts.length > 4 ? parts[4] : "";
					power1 += " " + bonus;
					String owner = (parts.length > 6 ? parts[6] : "");
					String alignment = parts[2];
					String power2 = (parts.length >= 6 ? parts[5] : "");
					ArtifactInfo artifact = new ArtifactInfo();
					artifact.setNo(no);
					artifact.setName(name);
					artifact.setOwner(owner);
					artifact.setAlignment(alignment);
					artifact.setPower(0, power1);
					if (!power2.equals("")) {
						artifact.setPower(1, power2);
					}
					artifacts.addItem(artifact);
				} catch (NumberFormatException e) {
					throw e;
				}
			}
		} catch (IOException exc) {
			throw exc;
		} catch (NumberFormatException exc) {
			throw new MetadataReaderException("Error reading artifact metadata.", exc);
		}
		return artifacts;
	}
}

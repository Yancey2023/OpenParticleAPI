package yancey.openparticle.api.run.data.identifier;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataIdentifier {

    public final String namespace, value;
    private Object particleSprites;

    public DataIdentifier(String namespace, String value) {
        this.namespace = namespace;
        this.value = value;
    }

    public DataIdentifier(String value) {
        this("minecraft", value);
    }

    public DataIdentifier(DataInputStream dataInputStream) throws IOException {
        this.namespace = dataInputStream.readBoolean() ? "minecraft" : dataInputStream.readUTF();
        this.value = dataInputStream.readUTF();
    }

    public void writeToFile(DataOutputStream dataOutputStream) throws IOException {
        if (Objects.equals(namespace, "minecraft")) {
            dataOutputStream.writeBoolean(true);
        } else {
            dataOutputStream.writeBoolean(false);
            dataOutputStream.writeUTF(namespace);
        }
        dataOutputStream.writeUTF(value);
    }

    public static void writeIdentifierList(List<DataIdentifier> identifierList, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(identifierList.size());
        for (DataIdentifier dataIdentifier : identifierList) {
            dataIdentifier.writeToFile(dataOutputStream);
        }
    }

    public static List<DataIdentifier> readIdentifierList(DataInputStream dataInputStream) throws IOException {
        List<DataIdentifier> result = new ArrayList<>();
        int size = dataInputStream.readInt();
        for (int i = 0; i < size; i++) {
            result.add(new DataIdentifier(dataInputStream));
        }
        return result;
    }

    @Override
    public String toString() {
        return namespace + ':' + value;
    }
}

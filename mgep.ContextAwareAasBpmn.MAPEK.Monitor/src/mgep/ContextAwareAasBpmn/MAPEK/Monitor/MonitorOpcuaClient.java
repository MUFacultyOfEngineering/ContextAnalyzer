package mgep.ContextAwareAasBpmn.MAPEK.Monitor;


import mgep.ContextAwareAasBpmn.Core.Tools;
import mgep.ContextAwareAasBpmn.RdfRepositoryManager.RDFDAL;
import mgep.ContextAwareAasBpmn.RdfRepositoryManager.RDFRepositoryManager;

public class MonitorOpcuaClient {

	public static void main(String[] args) throws Exception {
		var client = new OpcuaConsumer("opc.tcp://192.168.56.101:4840");
		System.out.println(client.ReadOpcuaValue(2, "https://mondragon.edu/object/brickpi/motor/move/variable/port"));
		System.out.println(client.ReadOpcuaValue(2, "https://mondragon.edu/object/robot/lego_color_sorter/status"));
		
	}
}

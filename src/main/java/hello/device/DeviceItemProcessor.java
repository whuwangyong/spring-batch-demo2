package hello.device;

import org.springframework.batch.item.ItemProcessor;

/**
 * Created by wuzhaofeng on 17/7/23.
 */
public class DeviceItemProcessor implements ItemProcessor<DeviceCommand, DeviceCommand> {

    @Override
    public DeviceCommand process(DeviceCommand deviceCommand) throws Exception {

//        if(deviceCommand.getId().equals("8")) {
//            throw new Exception("error! 8");
//        }
        // 模拟下发命令给设备
        System.out.println("send command to device, id=" + deviceCommand.getId());

        // 更新命令状态
        deviceCommand.setStatus("SENT");

        Thread.sleep(1000);

        // 返回命令对象
        return deviceCommand;

    }

}

import com.crazychat.chat.ChatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@org.springframework.boot.test.context.SpringBootTest(classes = {ChatApplication.class})
public class SpringBootTest {

    @Test
    public void test() {
        System.out.println("2019-03-12T16:00:00.000Z".substring(0, 10));
    }
}

//package app.service;
//
//import app.dto.NotificationRequest;
//import app.entities.NotificationDelivery;
//import app.entities.NotificationDeliveryStatus;
//import app.entities.NotificationType;
//import com.icegreen.greenmail.util.GreenMail;
//import com.icegreen.greenmail.util.ServerSetup;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.mail.internet.MimeMessage;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
///**
// * Integration test for the happy path of notification delivery.
// */
//@SpringBootTest
//@ActiveProfiles("test")
//class NotificationDeliveryServiceIntegrationTest {
//
//    @Autowired
//    private NotificationDeliveryService notificationDeliveryService;
//
//    @Autowired
//    private NotificationDeliveryTxService notificationDeliveryTxService;
//
//
//
//    private GreenMail greenMail;
//
//    @BeforeEach
//    void setUp() {
//        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
//        greenMail.start();
//    }
//
//    @AfterEach
//    void tearDown() {
//        greenMail.stop();
//    }
//
//    @Test
//    @Transactional
//    void happyPathDeliversNotificationSuccessfully() throws Exception {
//        // Given
//        NotificationRequest request = new NotificationRequest(
//                "Dimitrije Tomic",
//                "test@example.com",
//                Map.of("name", "Dimitrije", "activationLink", "http://example.com/activate")
//        );
//
//        // When
//        notificationDeliveryService.handleIncomingMessage(request, "employee.created");
//
//        // Wait for async processing
//        Thread.sleep(2000); // Simple wait, in real test use Awaitility
//
//        // Then
//        MimeMessage[] messages = greenMail.getReceivedMessages();
//        assertEquals(1, messages.length);
//        assertEquals("Activation Email", messages[0].getSubject());
//
//        // Check database
//        List<NotificationDelivery> deliveries = notificationDeliveryTxService.findAllByStatus(NotificationDeliveryStatus.SUCCEEDED);
//        assertEquals(1, deliveries.size());
//        NotificationDelivery delivery = deliveries.get(0);
//        assertEquals(NotificationDeliveryStatus.SUCCEEDED, delivery.getStatus());
//        assertNotNull(delivery.getSentAt());
//    }
//}

# WhatsApp Web Automation (Java + Selenium)

This project automates sending a WhatsApp message (and optionally a document) through WhatsApp Web, inspired by the linked article.

## Prerequisites

- Java 17+
- Maven 3.9+
- Google Chrome installed

## Setup

1. Clone the repo and install dependencies:

```bash
mvn clean install
```

2. Set environment variables for the message you want to send:

```bash
export WHATSAPP_PHONE="15551234567"     # Country code + number, no '+'
export WHATSAPP_MESSAGE="Hello from Selenium!"
export WHATSAPP_FILE_PATH="/path/to/document.pdf"  # Optional
```

3. Run the automation:

```bash
mvn -q exec:java
```

## How it works

- Opens WhatsApp Web with a prefilled chat and message.
- Waits for you to scan the QR code (first run) and load the chat.
- Sends the text message.
- If `WHATSAPP_FILE_PATH` is set, uploads the file and clicks send.

## Notes

- WhatsApp Web UI changes periodically. If selectors break, update them in
  `WhatsAppAutomation.java`.
- The first time you run it, the browser will pause on the QR login screen.
  Scan with your phone to continue.
- Use responsibly and in accordance with WhatsApp's terms.

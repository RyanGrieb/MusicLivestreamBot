FROM maven:3.8.3-openjdk-16-slim
WORKDIR /app
COPY . .
RUN mvn clean package
CMD ["java", "-cp", "target/classes", "org.team_m.mlb.MusicLivestreamBot"]

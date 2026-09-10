# spring-tutorial-24th
CEOS 백엔드 24기 스프링 튜토리얼

## 1️⃣ spring이 지원하는 기술들(IoC/DI, AOP, PSA 등)을 자유롭게 조사해요

스프링에는 세가지 핵심 기술이 있습니다. 스프링 삼각형이라고도 부르는 3대 핵심 요소는 IoC/DI, AOP, PSA로, 이들은 서로 매우 긴밀하게 협력하여 동작합니다. 이들의 공통된 최종 목표는 순수한 자바 객체(POJO)를 유지하면서 결합도를 낮추고 유연한 서버 애플리케이션을 만드는 것입니다.

<img src="img/spring_triangle.png" width="30%">

근데? 사실 백번 읽어봐도 모르겠으니! 하나씩 공부해봅시다.

### ① IoC/DI

IoC/DI는 제어의 역전(Inversion of Control)과 의존성 주입(Dependency Injection)의 줄임말입니다.
Spring에서는 **IoC 컨테이너**가 객체 간의 의존성을 주입합니다.
여기서 '의존성 주입'은 '제어의 역전'의 특수한 형태입니다.
<br><br>
예시를 통해 조금 더 쉽게 살펴보도록 하겠습니다.
<br>
요리사가 국수를 요리하는 상황을 자바 코드로 비교해봅시다.

```java
public class Chef {
private PorkSpine meat;

    public Chef() {
        // 요리사가 직접 구체적인 객체를 생성합니다 (제어권이 요리사에게 있음)
        this.meat = new PorkSpine(); 
    }

    public void cook() {
        System.out.println("푹 고아낸 " + meat.getName() + " 국수 완성!");
    }
}
```
위 코드에서, 요리사는 직접 구체적인 객체를 생성합니다. 즉, 제어권이 요리사에게 있죠. 하지만 이 경우 돼지등뼈가 소진되면 어떻게 해야 할까요? 요리사가 직접 정육점까지 뛰어가 돼지등뼈를 사와야 하는 상황이 발생합니다.
<br>
하지만 요리사가 아닌, 사장님이 출근길에 고기를 사오면 훨씬 좋지 않을까요? 그리고 돼지등뼈가 없어도, 다른 고기를 넣을 수 있으면 더 편하지 않을까요?

```java
@Component
public class Chef {
    private final Meat meat;

    // 요리사는 추상적인 '고기(Meat 인터페이스)'면 뭐든 받아서 요리합니다.
    // 객체 생성과 공급의 제어권(IoC)이 스프링 사장님에게 넘어갔습니다!
    @Autowired 
    public Chef(Meat meat) {
        this.meat = meat; 
    }

    public void cook() {
        System.out.println("푹 고아낸 " + meat.getName() + " 국수 완성!");
    }
}
```
식당 사장님, 즉 스프링 컨테이너에게 제어권을 넘기면 요리사는 "고기"이기만 하면 뭐든 받아 요리하면 되는 편리한 상황이 됩니다.
또한 코드가 인터페이스에만 의존하게 되어 결합도가 낮아지죠.
<br>
위 예시 코드에서 본 방식은 생성자 주입(Constructor Injection) 방식입니다.
<br>
사실 의존성을 주입하는 방법에는 생성자 주입 뿐만 아니라 수정자 주입(Setter Injection), 필드 주입(Field Injection) 방식도 있습니다. 하지만 공식 문서에서는 특히 생성자 주입 방식을 권장합니다.
- 우선 생성 시점에 딱 한 번만 호출되므로 주입받은 의존성이 런타임에 변하지 않음(**불변성**)을 보장합니다.
- 또한 필드에 final 키워드를 사용할 수 있어, 만약 의존성 주입을 깜빡하더라도 애플리케이션 실행 전 컴파일 단계에서 바로 에러를 잡아낼 수 있습니다. 즉, **필수 의존성**을 보장하는거죠.
- 그 외에도 순환 참조를 방지할 수 있고, 테스트 시에 Mock 객체를 사용하기에 용이하다는 등의 이점이 있습니다.

그럼 이제 제어가 역전된다는게 어떤 느낌인지 알겠죠?
<br>
만약 아직도 이해가 안된다면 [의존성 주입 3분만에 이해하기 (Dependency Injection, Inversion of Control)](https://youtu.be/1vdeIL2iCcM?si=_hvHcxvKs-Z8tq3b) 영상을 추천드립니다.
<br>
이해가 되셨다면! 실제 스프링에서는 아래와 같이 제어의 역전이 사용됩니다. 

```java
import org.springframework.stereotype.Service;

@Service
public class UserService {
    // 구체적인 구현체가 아닌 인터페이스에 의존
    private final UserRepository userRepository;

    // 스프링 컨테이너가 UserService를 생성할 때 UserRepository 구현체를 자동으로 주입 (DI)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public void registerUser() {
        userRepository.save();
    }
}

```

### ② AOP

자, 이제 AOP입니다. 여러분은 모두 AOP를 잘 사용하고 있을겁니다. 자주 쓰이는 `@Transactional` 또한 AOP의 일종이라고 할 수 있기 때문이죠.
이런 AOP는 Aspect-Oriented Programming의 줄임말데요, 말 그대로 관점 지향 프로그래밍을 뜻합니다.
AOP, 관점 지향 프로그래밍이라는 말만 들으면 OOP, 객체 지향 프로그래밍과 공존할 수 없는 것처럼 들릴 수 있습니다. 하지만 AOP는 OOP 프로그래밍을 보완하는 구조입니다.
실제로 [Spring의 공식 문서](https://docs.spring.io/spring-framework/reference/core/aop.html#page-title)에는 다음과 같이 쓰여있습니다.

> Aspect-oriented Programming (AOP) complements Object-oriented Programming (OOP) by providing another way of thinking about program structure.

그리고 관점 지향은 쉽게 말해, '핵심 비즈니스 로직'과 '반복되는 부가 기능'을 완벽하게 분리해 내는 기술입니다. 즉, 애플리케이션 전반에 흩어져 있는 교차 관심사(ex. 로깅, 보안, 트랜잭션)를 별도의 모듈(Aspect)로 분리합니다.
<br><br>
예를 들어 은행 시스템을 개발한다고 상상해 볼까요?
<br><br>
<img src="img/spring_aop.png" width="70%">
<br><br>
'계좌 이체', '대출 승인', '잔액 조회'처럼 각 모듈이 수행해야 하는 진짜 비즈니스 로직이 있습니다. 반면, '보안 검사', 'DB 연동(트랜잭션)', '실행 시간 로깅' 같은 인프라 로직은 이체, 대출, 조회 기능을 실행할 때마다 공통으로 앞뒤에 들어가야 하죠.
<br>
이런 여러 고유 기능들(세로)을 가로지르며(횡단하며) 똑같이 나타나는 로직들을 '횡단 관심사(Cross-cutting Concerns)'라고 부릅니다.
AOP는 이렇게 가로로 겹치는 횡단 관심사들을 가위로 오려내어 별도의 클래스(Aspect)에 따로 모아둡니다. 그리고 프레임워크에 이 코드들을 런타임 시 앞뒤로 끼워 넣어달라고 설정해둡니다.
결과적으로 개발자는 복잡한 인프라 코드를 신경 쓸 필요 없이, '계좌 이체'라는 순수한 핵심 로직 작성에만 100% 집중할 수 있게 되는거죠.
<br>
만약 AOP가 없다면 개발자는 계좌 이체 코드에도, 대출 승인 코드에도 매번 보안검사(), 트랜잭션시작(), 로깅() 코드를 똑같이 복사해서 붙여넣어야 할겁니다. 보안 검사 방식이 업데이트라도 되면 수백 개의 파일을 열어 일일이 수정해야 하는 끔찍한 일이 벌어지고, 비즈니스 로직은 단 3줄인데, 부가 기능 코드가 20줄을 차지해 코드를 읽기도 힘들어지게 됩니다.
<br><br>
그러면 AOP는 어떻게 적용할 수 있을까요? AOP를 실제 코드에 적용하는 방식은 시점에 따라 크게 컴파일 타임, 클래스 로드 타임, 런타임 세 가지로 나뉩니다. 그리고 Spring AOP는 이 중 프록시 패턴을 기반으로 한 런타임(Run-time) 방식을 사용합니다.
스프링 컨테이너가 IoC/DI를 통해 객체(Bean)의 생성과 의존성 주입을 직접 통제하기 때문에, 실제 객체 대신 부가 기능이 씌워진 프록시 객체를 런타임에 동적으로 주입할 수 있는거죠.
<br>
아래와 같은 예시 코드가 AOP를 잘 보여줍니다.

```java
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Service 클래스의 모든 메서드 실행 전후에 공통 로깅 기능 적용
    @Around("execution(* com.example..*Service.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        Object proceed = joinPoint.proceed(); // 실제 핵심 비즈니스 로직 실행
        
        long executionTime = System.currentTimeMillis() - start;
        System.out.println(joinPoint.getSignature() + " 실행 시간: " + executionTime + "ms");
        return proceed;
    }
}
```

더 나은 AOP의 이해를 위해 [[10분 테코톡] 🌕제이의 Spring AOP](https://youtu.be/Hm0w_9ngDpM?si=hRbn6y1DWXYCic4F) 영상을 추천드립니다.

### ③ PSA

마지막으로 PSA입니다. PSA는 Portable Service Abstraction의 줄임말로, 일관된 서비스 추상화라고도 합니다.

쉽게 말하면 PSA는 어떤 기술을 사용하든 개발자가 비슷한 방식으로 사용할 수 있도록 중간에 추상화 계층을 만들어주는 것입니다.

예를 들어 우리가 해외여행을 간다고 생각해봅시다.
<br>
한국에서는 콘센트에 충전기를 바로 꽂을 수 있지만, 다른 나라에서는 콘센트 모양이나 전압이 다를 수 있습니다. 그렇다고 여행을 갈 때마다 노트북이나 휴대폰의 충전 방식을 전부 고칠 수는 없겠죠.
그래서 우리는 어댑터를 사용합니다. 사용자는 똑같은 충전기를 사용하고, 어댑터가 각 나라의 콘센트 규격에 맞게 변환해주죠.

Spring의 PSA도 비슷합니다.
개발자가 특정 기술의 세부적인 사용 방법을 일일이 알지 못하더라도, Spring이 제공하는 일관된 인터페이스를 사용하면 내부 구현 기술을 비교적 자유롭게 변경할 수 있도록 만들어줍니다.

대표적인 예가 바로 트랜잭션 추상화입니다.
앞의 AOP 설명에서 나왔던 `@Transactional`을 다시 생각해볼까요?

```java
@Service
public class OrderService {
    
    @Transactional
    public void createOrder() {
        // 주문 저장
        // 결제 처리
        // 재고 감소
    }
}
```

개발자인 우리는 그냥 `@Transactional`을 붙입니다.
<br>
그런데 실제로 트랜잭션을 처리하는 기술은 프로젝트마다 다를 수 있습니다.
어떤 프로젝트에서는 JDBC를 사용할 수도 있고, 다른 프로젝트에서는 JPA를 사용할 수도 있고, 또 다른 환경에서는 JTA와 같은 트랜잭션 기술을 사용할 수도 있습니다.
만약 Spring의 추상화가 존재하지 않는다면 사용하는 기술이 바뀔 때마다 개발자가 트랜잭션 시작, 커밋, 롤백을 처리하는 코드를 해당 기술의 API에 맞게 다시 작성해야 할 수도 있습니다.
하지만 Spring은 이들 사이에 트랜잭션 추상화 계층을 제공합니다.
그 중심에 있는 인터페이스가 `PlatformTransactionManager`입니다.

```java
public interface PlatformTransactionManager extends TransactionManager {
    
    TransactionStatus getTransaction(TransactionDefinition definition)
            throws TransactionException;
    
    void commit(TransactionStatus status)
            throws TransactionException;
    
    void rollback(TransactionStatus status)
            throws TransactionException;
}
```

실제로 사용하는 기술에 따라서 이 인터페이스의 구현체가 달라집니다.
예를 들어 JPA를 사용한다면 `JpaTransactionManager`가 사용될 수 있고, JDBC 기반 환경이라면 JDBC에 맞는 TransactionManager가 사용될 수 있습니다.
중요한 것은 비즈니스 로직을 작성하는 개발자가 이러한 차이를 직접 처리하지 않아도 된다는거죠.
Spring의 공식 문서에서도 Spring의 트랜잭션 지원이 JDBC, JTA, Hibernate, JPA처럼 서로 다른 트랜잭션 API에 대해 일관된 프로그래밍 모델을 제공한다고 설명합니다.

즉, 우리의 비즈니스 코드는 그대로 두고, 내부에서 사용하는 트랜잭션 기술이나 구현체를 변경할 수 있게 되는 것입니다.
덕분에 애플리케이션의 상위 계층이 특정 DB 기술의 예외 클래스에 강하게 의존하는 것을 줄일 수 있습니다.

결국 한줄로 정리하면, PSA의 핵심은 "구체적인 기술은 바뀌어도, 그것을 사용하는 애플리케이션의 코드는 최대한 바뀌지 않도록 만드는 것"이 아닐까요?

---

## 2️⃣ Spring Bean 이 무엇이고, Bean 의 라이프사이클과 Bean Scope에 대해 조사해요

### 1. Spring Bean, Lifecycle, Bean Scope

- **Spring Bean 이란?**<br>
  스프링 IoC 컨테이너에 의해 인스턴스화되고, 조립되며, 관리되는 객체
- **Bean Lifecycle (생명주기)**<br>
  - 스프링 컨테이너는 빈의 생성부터 소멸까지의 과정을 관리하며, 특정 시점에 개발자가 개입할 수 있는 콜백(Callback)을 제공
  - **과정:** 인스턴스화 → 의존성 주입(DI) → 초기화 콜백(Initialization) → 빈 사용 → 소멸 콜백(Destruction)
- **Bean Scope (스코프)**
  - 빈이 생성되고 존재하는 범위
  - **`singleton` (기본값):** 스프링 컨테이너당 단 하나의 공유 인스턴스만 생성되는 것
  - **`prototype`:** 컨테이너에 빈을 요청(주입)할 때마다 매번 새로운 인스턴스를 생성하는 것
  - **웹 전용 스코프:** 웹 환경에서만 유효하며 `request`(HTTP 요청당 하나), `session`(HTTP 세션당 하나), `application`(ServletContext당 하나), `websocket` 등

### 2. Java 어노테이션(Annotation)과 구현

- 어노테이션은 프로그램 소스 코드에 추가할 수 있는 메타데이터(Metadata)
- 프로그램의 비즈니스 로직 자체에는 직접적인 영향을 주지 않지만, 컴파일러에게 정보를 제공하거나 런타임 시 특정 기능을 수행하도록 프레임워크에 힌트를 준다.
- **Java에서의 구현:**
  - `@interface` 키워드를 사용하여 선언
  - **메타 어노테이션:** `@Target`(적용 대상: 클래스, 메서드, 필드 등)과 `@Retention`(유지 정책: Source, Class, Runtime)을 통해 동작 방식을 정의 
  - 스프링과 같은 프레임워크는 주로 `@Retention(RetentionPolicy.RUNTIME)`으로 설정된 어노테이션을 **리플렉션(Reflection) API**를 통해 런타임에 읽어들여 부가적인 처리를 수행

### 3. 스프링의 어노테이션 기반 Bean 등록 과정

스프링에서 `@Component`나 `@Bean` 어노테이션을 사용해 빈을 등록할 때 컨테이너 내부에서 일어나는 과정

1. **메타데이터 읽기:** 설정 클래스(예: `@Configuration`이 붙은 클래스) 읽기
2. **BeanDefinition 생성:** 컨테이너는 즉시 객체를 생성하지 않고, 어노테이션 정보를 바탕으로 빈의 설계도격인 **`BeanDefinition`** 객체를 생성. 여기에는 빈의 클래스 이름, 스코프, 초기화 메서드, 의존성 정보 등이 담김.
3. **레지스트리 등록:** 생성된 `BeanDefinition`을 컨테이너 내부의 `BeanDefinitionRegistry`에 등록
4. **인스턴스화 및 의존성 주입:** 빈 생성이 필요한 시점(싱글톤의 경우 컨테이너 초기화 시점)에 `BeanDefinition`을 기반으로 실제 Java 객체를 생성(Reflection 활용)하고, 필요한 의존성을 주입(DI)

### 4. `@ComponentScan`의 컴포넌트 탐색 과정 심층 분석

`@ComponentScan`: 스프링이 어디서부터 컴포넌트를 찾을지(`basePackages`) 지정하고, 이를 자동으로 빈으로 등록하는 기능

1. **클래스패스 스캐닝 (Classpath Scanning):** `ClassPathBeanDefinitionScanner`가 지정된 베이스 패키지와 그 하위 패키지의 디렉토리를 파일 시스템이나 JAR 파일 내의 클래스패스에서 스캔
2. **ASM을 통한 바이트코드 분석 (클래스 로딩 방지):** 스프링은 스캔 과정에서 메모리 낭비와 부작용을 막기 위해 런타임에 클래스를 직접 메모리에 로딩(Class Loading)하지 않고 대신 ASM이라는 바이트코드 조작 라이브러리를 기반으로 한 `MetadataReader`를 사용하여 `.class` 파일의 메타데이터만 빠르게 읽음
3. **필터링 (Filtering):** 읽어들인 메타데이터 중 `@Component` 어노테이션(또는 이를 포함한 `@Service`, `@Repository`, `@Controller` 등의 메타 어노테이션)이 존재하는지 확인
4. **BeanDefinition 등록:** 필터를 통과한 클래스들에 대해 `BeanDefinition`을 생성하고 레지스트리에 등록하여 이후 컨테이너가 빈으로 관리하게 함

---

## 3️⃣ 🔥Spring MVC를 심층 분석해요🔥

### 1. MVC 패턴 vs. Spring MVC

- **MVC 패턴 (Model-View-Controller):**<br>
  소프트웨어 공학에서 애플리케이션을 세 가지 역할로 나누는 **설계 아키텍처**로, 데이터(Model), 사용자 인터페이스(View), 그리고 이 둘을 연결하며 제어하는 비즈니스 로직(Controller)을 분리하여 유지보수성을 높이는 것이 목적
- **Spring MVC (`spring-webmvc`):**<br>
  스프링 프레임워크가 제공하는 웹 모듈로, MVC 아키텍처 패턴을 Java의 Servlet API를 기반으로 구현한 **실제 프레임워크**. 일반적인 MVC 구조에 중앙 집중식 요청 처리기인 **프론트 컨트롤러(Front Controller)** 패턴을 도입하여 웹 요청 처리를 표준화하고 유연한 확장을 제공.

### 2. Servlet과 웹 요청 처리

* **Servlet (서블릿):** Java를 사용하여 웹 서버의 기능을 확장하고 동적인 HTTP 응답을 생성하기 위한 자바 표준 인터페이스(규약)<br>
  (Spring MVC 자체가 이 Servlet API를 기반으로 구축되어 있음)
* **웹 요청 처리 흐름:**
  1. 클라이언트(브라우저)가 HTTP 요청을 서버로 전송
  2. 서블릿 컨테이너가 요청을 받아 `HttpServletRequest`와 `HttpServletResponse` 객체를 생성
  3. 요청 URL에 매핑된 서블릿의 `service()` 메서드를 호출. (HTTP 메서드에 따라 내부적으로 `doGet`, `doPost` 등으로 분기)
  4. 로직 처리가 끝나면 `HttpServletResponse`를 통해 클라이언트에게 응답을 보내고, 생성되었던 Request/Response 객체를 소멸시킴

### 3. 톰캣(Tomcat)과 WAS

* **WAS (Web Application Server):** 정적 콘텐츠(HTML, 이미지)만 제공하는 웹 서버와 달리, DB 조회나 복잡한 비즈니스 로직을 통해 **동적인 콘텐츠**를 생성하고 제공하는 미들웨어. 내부적으로 서블릿을 실행할 수 있는 '서블릿 컨테이너'를 포함.
* **Tomcat:** 아파치 소프트웨어 재단에서 개발한 가장 대표적인 오픈소스 서블릿 컨테이너이자 WAS. 스프링 부트(Spring Boot)는 이 톰캣을 애플리케이션 내부에 내장(Embedded Tomcat)하여 제공하므로, 개발자가 별도의 서버 설정 없이 `main()` 메서드 실행만으로 웹 서버를 띄울 수 있음.

### 4. DispatcherServlet과 웹 요청 동작 흐름

스프링 공식 문서는 Spring MVC의 핵심을 `DispatcherServlet`으로 정의함. 이는 모든 HTTP 요청을 가장 먼저 받아 적절한 컴포넌트로 요청을 위임하는 **프론트 컨트롤러(Front Controller)** 역할을 수행.

**동작 흐름:**

1. 요청 수신
2. 핸들러 조회 (`HandlerMapping`)
3. 어댑터 조회 및 실행 (`HandlerAdapter`)
4. 결과 처리 및 응답 반환

---

이번 과제를 수행하면서 관련된 개념들에 대한 유튜브 영상들로부터 많은 도움을 받았습니다. 제가 집중력이 짧아 영상의 도움을 받은 것도 있지만... 생각보다 유튜브에 고수들이 너무나 쉽게 풀어서 설명한 영상들이 참 많답니다? 모두 알고 계시겠지만 한번 상기시켜드리고 갑니다...👍
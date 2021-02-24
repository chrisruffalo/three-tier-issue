# Issue: Invoking chained EJB Calls Causes SSL Exception

## Issue
There is an issue when using EJB calls that use "pure http" remoting with HTTPS where the first EJB can invoke the
second remote EJB but there is an exception thrown when the second remote EJB invokes the third.

## Impacted Versions
* EAP <= 7.3.5.GA
* Wildfly <= 22.0.1.Final 

## Outline
This is a minimal reproduction that configures and launch EAP. It includes three EJBs and a REST service to start the
invocation. The rest invocation protocol (http/https) is propagated for all requests to see the difference between
the two (secure/nonsecure) invocation types. There is also a second EJB and endpoint that can be used to recur
the call chain to itself to demonstrate the behavior.

The order of actions is:
* REST Service on host ONE
* Local EJB invocation on host ONE
* Remote EJB invocation on host TWO
* Remote EJB invocation on host THREE
* Exception (see Exceptions for detail)

## Exceptions
The following exception (or one like it) is thrown when the call for the third tier fails:
```bash
11:00:47,734 ERROR [io.undertow.request] (default task-2) UT005023: Exception handling request to /tier/api/recur: org.jboss.resteasy.spi.UnhandledException: javax.ejb.EJBTransactionRolledbackException: javax.transaction.RollbackException: ARJUNA016053: Could not commit transaction.
	at org.jboss.resteasy.core.ExceptionHandler.handleApplicationException(ExceptionHandler.java:82)
	at org.jboss.resteasy.core.ExceptionHandler.handleException(ExceptionHandler.java:346)
	at org.jboss.resteasy.core.SynchronousDispatcher.writeException(SynchronousDispatcher.java:193)
	at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:457)
	at org.jboss.resteasy.core.SynchronousDispatcher.lambda$invoke$4(SynchronousDispatcher.java:229)
	at org.jboss.resteasy.core.SynchronousDispatcher.lambda$preprocess$0(SynchronousDispatcher.java:135)
	at org.jboss.resteasy.core.interception.PreMatchContainerRequestContext.filter(PreMatchContainerRequestContext.java:358)
	at org.jboss.resteasy.core.SynchronousDispatcher.preprocess(SynchronousDispatcher.java:138)
	at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:215)
	at org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher.service(ServletContainerDispatcher.java:245)
	at org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.service(HttpServletDispatcher.java:61)
	at org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.service(HttpServletDispatcher.java:56)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:590)
	at io.undertow.servlet.handlers.ServletHandler.handleRequest(ServletHandler.java:74)
	at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:129)
	at io.opentracing.contrib.jaxrs2.server.SpanFinishingFilter.doFilter(SpanFinishingFilter.java:52)
	at io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
	at io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
	at io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
	at io.undertow.servlet.handlers.ServletChain$1.handleRequest(ServletChain.java:68)
	at io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
	at org.wildfly.extension.undertow.security.SecurityContextAssociationHandler.handleRequest(SecurityContextAssociationHandler.java:78)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.servlet.handlers.RedirectDirHandler.handleRequest(RedirectDirHandler.java:68)
	at io.undertow.servlet.handlers.security.SSLInformationAssociationHandler.handleRequest(SSLInformationAssociationHandler.java:132)
	at io.undertow.servlet.handlers.security.ServletAuthenticationCallHandler.handleRequest(ServletAuthenticationCallHandler.java:57)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.security.handlers.AbstractConfidentialityHandler.handleRequest(AbstractConfidentialityHandler.java:46)
	at io.undertow.servlet.handlers.security.ServletConfidentialityConstraintHandler.handleRequest(ServletConfidentialityConstraintHandler.java:64)
	at io.undertow.security.handlers.AuthenticationMechanismsHandler.handleRequest(AuthenticationMechanismsHandler.java:60)
	at io.undertow.servlet.handlers.security.CachedAuthenticatedSessionHandler.handleRequest(CachedAuthenticatedSessionHandler.java:77)
	at io.undertow.security.handlers.NotificationReceiverHandler.handleRequest(NotificationReceiverHandler.java:50)
	at io.undertow.security.handlers.AbstractSecurityContextAssociationHandler.handleRequest(AbstractSecurityContextAssociationHandler.java:43)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at org.wildfly.extension.undertow.security.jacc.JACCContextIdHandler.handleRequest(JACCContextIdHandler.java:61)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at org.wildfly.extension.undertow.deployment.GlobalRequestControllerHandler.handleRequest(GlobalRequestControllerHandler.java:68)
	at io.undertow.servlet.handlers.SendErrorPageHandler.handleRequest(SendErrorPageHandler.java:52)
	at io.undertow.server.handlers.PredicateHandler.handleRequest(PredicateHandler.java:43)
	at io.undertow.servlet.handlers.ServletInitialHandler.handleFirstRequest(ServletInitialHandler.java:269)
	at io.undertow.servlet.handlers.ServletInitialHandler.access$100(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandler$2.call(ServletInitialHandler.java:133)
	at io.undertow.servlet.handlers.ServletInitialHandler$2.call(ServletInitialHandler.java:130)
	at io.undertow.servlet.core.ServletRequestContextThreadSetupAction$1.call(ServletRequestContextThreadSetupAction.java:48)
	at io.undertow.servlet.core.ContextClassLoaderSetupAction$1.call(ContextClassLoaderSetupAction.java:43)
	at org.wildfly.extension.undertow.security.SecurityContextThreadSetupAction.lambda$create$0(SecurityContextThreadSetupAction.java:105)
	at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1530)
	at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1530)
	at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1530)
	at org.wildfly.extension.undertow.deployment.UndertowDeploymentInfoService$UndertowThreadSetupAction.lambda$create$0(UndertowDeploymentInfoService.java:1530)
	at io.undertow.servlet.handlers.ServletInitialHandler.dispatchRequest(ServletInitialHandler.java:249)
	at io.undertow.servlet.handlers.ServletInitialHandler.access$000(ServletInitialHandler.java:78)
	at io.undertow.servlet.handlers.ServletInitialHandler$1.handleRequest(ServletInitialHandler.java:99)
	at io.undertow.server.Connectors.executeRootHandler(Connectors.java:387)
	at io.undertow.server.HttpServerExchange$1.run(HttpServerExchange.java:841)
	at org.jboss.threads.ContextClassLoaderSavingRunnable.run(ContextClassLoaderSavingRunnable.java:35)
	at org.jboss.threads.EnhancedQueueExecutor.safeRun(EnhancedQueueExecutor.java:1990)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.doRunTask(EnhancedQueueExecutor.java:1486)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1377)
	at org.xnio.XnioWorker$WorkerThreadFactory$1$1.run(XnioWorker.java:1280)
	at java.lang.Thread.run(Thread.java:748)
Caused by: javax.ejb.EJBTransactionRolledbackException: javax.transaction.RollbackException: ARJUNA016053: Could not commit transaction.
	at org.jboss.as.ejb3.tx.CMTTxInterceptor.endTransaction(CMTTxInterceptor.java:115)
	at org.jboss.as.ejb3.tx.CMTTxInterceptor.invokeInOurTx(CMTTxInterceptor.java:282)
	at org.jboss.as.ejb3.tx.CMTTxInterceptor.required(CMTTxInterceptor.java:388)
	at org.jboss.as.ejb3.tx.CMTTxInterceptor.processInvocation(CMTTxInterceptor.java:158)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.invocation.InterceptorContext$Invocation.proceed(InterceptorContext.java:509)
	at org.jboss.weld.module.ejb.AbstractEJBRequestScopeActivationInterceptor.aroundInvoke(AbstractEJBRequestScopeActivationInterceptor.java:72)
	at org.jboss.as.weld.ejb.EjbRequestScopeActivationInterceptor.processInvocation(EjbRequestScopeActivationInterceptor.java:89)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ejb3.component.interceptors.CurrentInvocationContextInterceptor.processInvocation(CurrentInvocationContextInterceptor.java:41)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ejb3.component.invocationmetrics.WaitTimeInterceptor.processInvocation(WaitTimeInterceptor.java:47)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ejb3.security.SecurityContextInterceptor.processInvocation(SecurityContextInterceptor.java:100)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ejb3.deployment.processors.StartupAwaitInterceptor.processInvocation(StartupAwaitInterceptor.java:22)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ejb3.component.interceptors.ShutDownInterceptorFactory$1.processInvocation(ShutDownInterceptorFactory.java:64)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ejb3.component.interceptors.LoggingInterceptor.processInvocation(LoggingInterceptor.java:67)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.as.ee.component.NamespaceContextInterceptor.processInvocation(NamespaceContextInterceptor.java:50)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.invocation.ContextClassLoaderInterceptor.processInvocation(ContextClassLoaderInterceptor.java:60)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.invocation.InterceptorContext.run(InterceptorContext.java:438)
	at org.wildfly.security.manager.WildFlySecurityManager.doChecked(WildFlySecurityManager.java:628)
	at org.jboss.invocation.AccessCheckingInterceptor.processInvocation(AccessCheckingInterceptor.java:57)
	at org.jboss.invocation.InterceptorContext.proceed(InterceptorContext.java:422)
	at org.jboss.invocation.ChainedInterceptor.processInvocation(ChainedInterceptor.java:53)
	at org.jboss.as.ee.component.ViewService$View.invoke(ViewService.java:198)
	at org.jboss.as.ee.component.ViewDescription$1.processInvocation(ViewDescription.java:191)
	at org.jboss.as.ee.component.ProxyInvocationHandler.invoke(ProxyInvocationHandler.java:81)
	at tier.recur.RecurLocal$$$view23.call(Unknown Source)
	at com.tier.one.service.Service.recur(Service.java:42)
	at com.tier.one.service.Service$Proxy$_$$_WeldClientProxy.recur(Unknown Source)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.jboss.resteasy.core.MethodInjectorImpl.invoke(MethodInjectorImpl.java:138)
	at org.jboss.resteasy.core.ResourceMethodInvoker.internalInvokeOnTarget(ResourceMethodInvoker.java:543)
	at org.jboss.resteasy.core.ResourceMethodInvoker.invokeOnTargetAfterFilter(ResourceMethodInvoker.java:432)
	at org.jboss.resteasy.core.ResourceMethodInvoker.lambda$invokeOnTarget$0(ResourceMethodInvoker.java:393)
	at org.jboss.resteasy.core.interception.PreMatchContainerRequestContext.filter(PreMatchContainerRequestContext.java:358)
	at org.jboss.resteasy.core.ResourceMethodInvoker.invokeOnTarget(ResourceMethodInvoker.java:395)
	at org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:364)
	at org.jboss.resteasy.core.ResourceMethodInvoker.invoke(ResourceMethodInvoker.java:337)
	at org.jboss.resteasy.core.SynchronousDispatcher.invoke(SynchronousDispatcher.java:440)
	... 58 more
Caused by: javax.transaction.RollbackException: ARJUNA016053: Could not commit transaction.
	at com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple.commitAndDisassociate(TransactionImple.java:1307)
	at com.arjuna.ats.internal.jta.transaction.arjunacore.BaseTransaction.commit(BaseTransaction.java:126)
	at com.arjuna.ats.jbossatx.BaseTransactionManagerDelegate.commit(BaseTransactionManagerDelegate.java:94)
	at org.wildfly.transaction.client.LocalTransaction.commitAndDissociate(LocalTransaction.java:78)
	at org.wildfly.transaction.client.ContextTransactionManager.commit(ContextTransactionManager.java:71)
	at org.jboss.as.ejb3.tx.CMTTxInterceptor.endTransaction(CMTTxInterceptor.java:89)
	... 106 more
	Suppressed: javax.transaction.xa.XAException
		at org.wildfly.httpclient.transaction.HttpSubordinateTransactionHandle.processOperation(HttpSubordinateTransactionHandle.java:145)
		at org.wildfly.httpclient.transaction.HttpSubordinateTransactionHandle.processOperation(HttpSubordinateTransactionHandle.java:102)
		at org.wildfly.httpclient.transaction.HttpSubordinateTransactionHandle.commit(HttpSubordinateTransactionHandle.java:69)
		at org.wildfly.transaction.client.SubordinateXAResource.commit(SubordinateXAResource.java:181)
		at com.arjuna.ats.internal.jta.resources.arjunacore.XAResourceRecord.topLevelOnePhaseCommit(XAResourceRecord.java:702)
		at com.arjuna.ats.arjuna.coordinator.BasicAction.onePhaseCommit(BasicAction.java:2395)
		at com.arjuna.ats.arjuna.coordinator.BasicAction.End(BasicAction.java:1497)
		at com.arjuna.ats.arjuna.coordinator.TwoPhaseCoordinator.end(TwoPhaseCoordinator.java:96)
		at com.arjuna.ats.arjuna.AtomicAction.commit(AtomicAction.java:162)
		at com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple.commitAndDisassociate(TransactionImple.java:1295)
		... 111 more
	Caused by: java.io.IOException: UT000065: SSL must be specified to connect to a https URL
		at io.undertow.client.http.HttpClientProvider.connect(HttpClientProvider.java:72)
		at io.undertow.client.http.HttpClientProvider.connect(HttpClientProvider.java:60)
		at io.undertow.client.UndertowClient.connect(UndertowClient.java:137)
		at org.wildfly.httpclient.common.HttpConnectionPool.runPending(HttpConnectionPool.java:151)
		at org.wildfly.httpclient.common.HttpConnectionPool.getConnection(HttpConnectionPool.java:83)
		at org.wildfly.httpclient.common.HttpTargetContext.sendRequest(HttpTargetContext.java:142)
		at org.wildfly.httpclient.transaction.HttpSubordinateTransactionHandle.processOperation(HttpSubordinateTransactionHandle.java:112)
		... 120 more
```

There is another periodic exception that is thrown regardless of if the first exception is thrown:
```bash
10:52:53,842 WARN  [com.arjuna.ats.jta] (Periodic Recovery) ARJUNA016027: Local XARecoveryModule.xaRecovery got XA exception ARJUNA016099: Unknown error code:0: javax.transaction.xa.XAException: WFHTTP000005: Invalid response code 404 (full response ClientResponse{responseHeaders={content-length=[74], content-type=[text/html], date=[Wed, 28 Oct 2020 14:52:53 GMT]}, responseCode=404, status='', protocol=HTTP/2.0})
	at org.wildfly.httpclient.transaction.HttpRemoteTransactionPeer.recover(HttpRemoteTransactionPeer.java:125)
	at org.wildfly.transaction.client.SubordinateXAResource.recover(SubordinateXAResource.java:237)
	at org.wildfly.transaction.client.SubordinateXAResource.recover(SubordinateXAResource.java:233)
	at com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule.xaRecoveryFirstPass(XARecoveryModule.java:659)
	at com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule.periodicWorkFirstPass(XARecoveryModule.java:240)
	at com.arjuna.ats.internal.jta.recovery.arjunacore.XARecoveryModule.periodicWorkFirstPass(XARecoveryModule.java:182)
	at com.arjuna.ats.internal.arjuna.recovery.PeriodicRecovery.doWorkInternal(PeriodicRecovery.java:770)
	at com.arjuna.ats.internal.arjuna.recovery.PeriodicRecovery.run(PeriodicRecovery.java:382)
Caused by: java.io.IOException: WFHTTP000005: Invalid response code 404 (full response ClientResponse{responseHeaders={content-length=[74], content-type=[text/html], date=[Wed, 28 Oct 2020 14:52:53 GMT]}, responseCode=404, status='', protocol=HTTP/2.0})
	at org.wildfly.httpclient.common.HttpTargetContext$1$1.lambda$completed$4(HttpTargetContext.java:235)
	at org.jboss.threads.ContextClassLoaderSavingRunnable.run(ContextClassLoaderSavingRunnable.java:35)
	at org.jboss.threads.EnhancedQueueExecutor.safeRun(EnhancedQueueExecutor.java:1990)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.doRunTask(EnhancedQueueExecutor.java:1486)
	at org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1377)
	at org.xnio.XnioWorker$WorkerThreadFactory$1$1.run(XnioWorker.java:1280)
	at java.lang.Thread.run(Thread.java:748)
```

## Assumptions
* The tester is using a bash terminal.

## Setup

### Wildfly 21
* Execute `./setup.sh`

### EAP 7.x
* Downlaod EAP 7.x and unzip
* Rename the EAP 7.x root folder "wildfly"
* Optional: Apply any patches to the EAP installation 
* Execute `./setup.sh`

## Deploying Code
* Execute `./deploy.sh`

This script builds all the modules and then copies the module (tier-api) to the appropriate Wildfly directory and then deploys the tier-impl and tier-war artifacts to `wildfly/standalone/deployments`.

## Running The EAP Instances
* Execute `./run.sh`

## Executing the Test
Normal operation with HTTP
```bash
[]$ curl http://localhost:8080/tier/api/tier
Wed Oct 28 10:42:39 EDT 2020
```

Exception occurs with HTTPS
```bash
[]$ curl -k https://localhost:8443/tier/api/tier
< throws exception >
```

Other failures can be observed with the "recur" endpoint
```bash
[]$ curl http://localhost:8080/tier/api/recur\?depth=0\&remote=true
Wed Oct 28 10:42:39 EDT 2020
[]$ curl http://localhost:8080/tier/api/recur\?depth=1\&remote=true
Wed Oct 28 10:42:39 EDT 2020
[]$ curl http://localhost:8080/tier/api/recur\?depth=2\&remote=true
Wed Oct 28 10:42:39 EDT 2020
[]$ curl http://localhost:8080/tier/api/recur\?depth=3\&remote=true
< hangs indefinitely >
[]$ curl http://localhost:8080/tier/api/recur\?depth=10\&remote=true
< hangs indefinitely >
```

With ssl on "recur" endpoint:
```bash
[]$ curl -k https://localhost:8443/tier/api/recur\?depth=0\&remote=true
Wed Oct 28 11:02:29 EDT 202
[]$ curl -k https://localhost:8443/tier/api/recur\?depth=1\&remote=true
< throws exception >
[]$ curl -k https://localhost:8443/tier/api/recur\?depth=2\&remote=true
< throws exception >
[]$ curl -k https://localhost:8443/tier/api/recur\?depth=3\&remote=true
< throws exception >
[]$ curl -k https://localhost:8443/tier/api/recur\?depth=10\&remote=true
< throws exception >
[]$ curl -k https://localhost:8443/tier/api/recur\?depth=100\&remote=true
< hangs indefinitely >
```


package com.novoda.simplechromecustomtabs.connection;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;

import com.novoda.simplechromecustomtabs.provider.AvailableAppProvider;
import com.novoda.simplechromecustomtabs.provider.SimpleChromeCustomTabsAvailableAppProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import static com.novoda.simplechromecustomtabs.provider.SimpleChromeCustomTabsAvailableAppProvider.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class BinderTest {

    @Mock
    private Activity mockActivity;
    @Mock
    private ServiceConnectionCallback mockServiceConnectionCallback;
    @Mock
    private AvailableAppProvider mockAvailableAppProvider;
    @Captor
    private ArgumentCaptor<SimpleChromeCustomTabsAvailableAppProvider.PackageFoundCallback> packageFoundCallbackCaptor;

    private Binder binder;

    @Before
    public void setUp() {
        initMocks(this);

        packageFoundCallbackCaptor = ArgumentCaptor.forClass(PackageFoundCallback.class);
        binder = new Binder(mockAvailableAppProvider);
    }

    @Test
    public void bindsActivityToServiceIfNotAlreadyBoundAndPackageAvailable() {
        bindServiceGivenThatPackageIsAvailable();

        verify(mockActivity).bindService(any(Intent.class), any(ServiceConnection.class), anyInt());
    }

    @Test
    public void doesNotBindActivityToServiceIfAlreadyBound() {
        givenThatActivityIsAlreadyBound();

        binder.bindCustomTabsServiceTo(mockActivity, mockServiceConnectionCallback);

        verifyNoMoreInteractions(mockActivity);
    }

    @Test
    public void doesNotBindActivityToServiceIfPackageNotAvailable() {
        binder.bindCustomTabsServiceTo(mockActivity, mockServiceConnectionCallback);

        verifyNoMoreInteractions(mockActivity);
    }

    @Test
    public void unbindsActivityFromServiceIfAlreadyBound() {
        bindService();

        binder.unbindCustomTabsService(mockActivity);

        verify(mockActivity).unbindService(any(ServiceConnection.class));
    }

    @Test
    public void doesNotUnbindActivityFromServiceIfNotAlreadyBound() {
        binder.unbindCustomTabsService(mockActivity);

        verifyZeroInteractions(mockActivity);
    }

    private void bindService() {
        binder.bindCustomTabsServiceTo(mockActivity, mockServiceConnectionCallback);
    }

    private void givenThatActivityIsAlreadyBound() {
        bindService();
    }

    private void bindServiceGivenThatPackageIsAvailable() {
        bindService();

        verify(mockAvailableAppProvider).findBestPackage(packageFoundCallbackCaptor.capture());
        packageFoundCallbackCaptor.getValue().onPackageFound("anyPackage");
    }

}

package com.specmate.emfrest.authentication;

import javax.ws.rs.core.Response;

import org.eclipse.emf.ecore.EObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogService;

import com.specmate.auth.api.IAuthenticationService;
import com.specmate.common.RestResult;
import com.specmate.common.SpecmateException;
import com.specmate.emfrest.api.IRestService;
import com.specmate.emfrest.api.RestServiceBase;
import com.specmate.usermodel.User;
import com.specmate.usermodel.UserSession;

@Component(service = IRestService.class)
public class Login extends RestServiceBase {
	public static final String SERVICE_NAME = "login";

	private IAuthenticationService authService;
	private LogService logService;

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public boolean canPost(Object object2, EObject object) {
		return true;
	}

	@Override
	public RestResult<?> post(Object object, EObject object2, String token) throws SpecmateException {
		if (object2 instanceof User) {
			User user = (User) object2;
			try {
				// TODO: Remove the if stmt below (it's just for testing)
				if (user.getUserName().contains("invalid")) {
					throw new SpecmateException("Inavlid User");
				}

				UserSession session = authService.authenticate(user.getUserName(), user.getPassWord(),
						user.getProjectName());
				logService.log(LogService.LOG_INFO,
						"Session " + session.getId() + " for user " + user.getUserName() + " created.");
				return new RestResult<>(Response.Status.OK, session);

			} catch (SpecmateException e) {
				logService.log(LogService.LOG_INFO, e.getMessage());
				return new RestResult<>(Response.Status.FORBIDDEN);
			}
		} else {
			throw new SpecmateException("Invalid login data.");
		}
	}

	@Reference
	public void setAuthService(IAuthenticationService authService) {
		this.authService = authService;
	}

	@Reference
	public void setLogService(LogService logService) {
		this.logService = logService;
	}
}
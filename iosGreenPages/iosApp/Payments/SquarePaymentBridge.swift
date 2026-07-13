import Foundation
import UIKit
import SquareInAppPaymentsSDK
import Shared

@objc public class SquarePaymentBridge: NSObject, Shared.SquarePaymentBridge {

    @objc public static let shared = SquarePaymentBridge()

    private var completion: ((String?, String?) -> Void)?
    private var pendingNonce: String?
    private var sdkConfigured = false

    private override init() {
        super.init()
    }

    public func startPayment(
        amount: Int64,
        currency: String,
        onResult: @escaping (String?, String?) -> Void
    ) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            self.presentCardEntry(completion: onResult)
        }
    }

    @objc public func startPayment(
        amount: Int64,
        currency: String,
        completion: @escaping (String?, String?) -> Void
    ) {
        startPayment(amount: amount, currency: currency, onResult: completion)
    }

    private func presentCardEntry(
        completion: @escaping (String?, String?) -> Void
    ) {
        guard configureSDKIfNeeded() else {
            completion(nil, "Square application ID is missing")
            return
        }

        guard let presenter = topViewController() else {
            completion(nil, "No view controller available to present card entry")
            return
        }

        self.completion = completion
        self.pendingNonce = nil

        let cardEntryViewController = SQIPCardEntryViewController(
            theme: SQIPTheme()
        )
        cardEntryViewController.delegate = self

        let navController = UINavigationController(
            rootViewController: cardEntryViewController
        )
        navController.modalPresentationStyle = .formSheet
        presenter.present(navController, animated: true)
    }

    private func configureSDKIfNeeded() -> Bool {
        if sdkConfigured { return true }

        let applicationId = PlatformConfig.shared.squareApplicationId
        guard !applicationId.isEmpty else { return false }

        SQIPInAppPaymentsSDK.squareApplicationID = applicationId
        sdkConfigured = true
        return true
    }

    fileprivate func finish(
        with cardEntryViewController: SQIPCardEntryViewController,
        token: String?,
        error: String?
    ) {
        let callback = completion
        completion = nil
        pendingNonce = nil

        cardEntryViewController.dismiss(animated: true) {
            callback?(token, error)
        }
    }
}

extension SquarePaymentBridge: SQIPCardEntryViewControllerDelegate {

    public func cardEntryViewController(
        _ cardEntryViewController: SQIPCardEntryViewController,
        didObtain cardDetails: SQIPCardDetails,
        completionHandler: @escaping (Error?) -> Void
    ) {
        pendingNonce = cardDetails.nonce
        completionHandler(nil)
    }

    public func cardEntryViewController(
        _ cardEntryViewController: SQIPCardEntryViewController,
        didCompleteWith status: SQIPCardEntryCompletionStatus
    ) {
        switch status {
        case .success:
            finish(
                with: cardEntryViewController,
                token: pendingNonce,
                error: pendingNonce == nil ? "Square did not return a card nonce" : nil
            )
        case .canceled:
            finish(
                with: cardEntryViewController,
                token: nil,
                error: "Payment cancelled"
            )
        }
    }
}

private func topViewController(
    _ rootViewController: UIViewController? = UIApplication.shared
        .connectedScenes
        .compactMap { ($0 as? UIWindowScene)?.keyWindow }
        .first?
        .rootViewController
) -> UIViewController? {

    if let navigationController = rootViewController as? UINavigationController {
        return topViewController(navigationController.visibleViewController)
    }

    if let tabController = rootViewController as? UITabBarController {
        return topViewController(tabController.selectedViewController)
    }

    if let presented = rootViewController?.presentedViewController {
        return topViewController(presented)
    }

    return rootViewController
}

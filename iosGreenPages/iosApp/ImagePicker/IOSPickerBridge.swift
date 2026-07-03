import PhotosUI
import UIKit
import Foundation
import Shared

class IOSPickerBridge: NSObject, PHPickerViewControllerDelegate {

    static let instance = IOSPickerBridge()

    static var callback: ((NSData, String) -> Void)?

    static func pickImage(callback: @escaping (NSData, String) -> Void) {
        self.callback = callback

        let picker = PHPickerViewController(configuration: PHPickerConfiguration())
        picker.delegate = IOSPickerBridge.instance

        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?
            .rootViewController?
            .present(picker, animated: true)
    }

    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)

        guard let item = results.first else { return }

        item.itemProvider.loadDataRepresentation(forTypeIdentifier: "public.image") { data, _ in
            guard let data = data else { return }

            IOSPickerBridge.callback?(
                data as NSData,
                "img_ios.jpg"
            )
        }
    }
}


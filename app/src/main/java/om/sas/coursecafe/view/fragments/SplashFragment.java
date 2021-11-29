package om.sas.coursecafe.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import om.sas.coursecafe.R;

public class SplashFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_splash, container, false);

        ImageView ivLogoSplashCircle = parentView.findViewById(R.id.iv_logo_splash_circle);
        Animation animationSplashCircle = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_circle_anim);
        ivLogoSplashCircle.startAnimation(animationSplashCircle);

        ImageView ivLogoSplash = parentView.findViewById(R.id.iv_logo_splash);
        Animation animationSplash = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_logo_anim);
        ivLogoSplash.startAnimation(animationSplash);

        return parentView;
    }


}

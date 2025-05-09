package com.example.myapplication.childprofile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentChildProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.perf.FirebasePerformance

class ChildProfileFragment : Fragment() {
    private var _binding: FragmentChildProfileBinding? = null
    private val binding get() = _binding!!
    private var requestAdapter: RequestAdapter? = null

    companion object {
        private const val ARG_CHILD_ID = "childID"

        fun newInstance(childID: String?): ChildProfileFragment {
            val fragment = ChildProfileFragment()
            val args = Bundle()
            args.putString(ARG_CHILD_ID, childID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChildProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        val editButton = binding.editButton
        val recyclerView = binding.childRequestsRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val photosRecyclerView = binding.photosRecyclerView
        photosRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val childID = arguments?.getString(ARG_CHILD_ID)
        if (childID == null) {
            Toast.makeText(requireContext(), "Child ID is missing", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        editButton.setOnClickListener {
            val newFragment = EditChildProfileFragment.newInstance(childID)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }

        firestore.collection("children").document(childID).get()
            .addOnSuccessListener { childDoc ->
                val orphanageID = childDoc.getString("orphanageID")

                // Check employee role + orphanage match
                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { userDoc ->
                        val role = userDoc.getString("role")
                        val userOrphanageID = userDoc.getString("orphanageID")

                        if (role == "orphanage employee" && userOrphanageID == orphanageID) {
                            editButton.visibility = View.VISIBLE
                        } else {
                            editButton.visibility = View.GONE
                        }

                        val isAdmin = role == "admin"

                        requestAdapter = RequestAdapter(onDonateClick = { request ->
                            val fragment = DonateFragment.newInstance(request)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }, isAdmin = isAdmin)

                        recyclerView.adapter = requestAdapter

                        val trace = FirebasePerformance.getInstance().newTrace("load_reviews")
                        trace.start()

                        val name = childDoc.getString("childName") ?: "Unknown"
                        val age = childDoc.getLong("childAge")?.toInt() ?: -1
                        val bio = childDoc.getString("childBio") ?: "No bio available."
                        val photos = childDoc.get("photos") as? List<String>
                        val profilePhotoUrl = childDoc.getString("photoUrl")

                        binding.childName.text = name
                        binding.childAge.text = if (age > 0) "$age years old" else "Age unknown"
                        binding.bioText.text = bio
                        binding.aboutText.text = "\u2728 About $name"

                        if (!profilePhotoUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profilePhotoUrl)
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(binding.profileImage)
                        }

                        if (!photos.isNullOrEmpty()) {
                            val photoAdapter = PhotoAdapter(photos)
                            photosRecyclerView.adapter = photoAdapter
                        }

                        if (!orphanageID.isNullOrEmpty()) {
                            firestore.collection("orphanages").document(orphanageID).get()
                                .addOnSuccessListener { orphanageDoc ->
                                    binding.orphanageName.text =
                                        orphanageDoc.getString("orphanageName")
                                            ?: "Unknown orphanage"
                                }
                                .addOnFailureListener {
                                    binding.orphanageName.text = "Unknown orphanage"
                                }
                        } else {
                            binding.orphanageName.text = "No orphanage assigned"
                        }


                        val videoView = view.findViewById<VideoView>(R.id.dream_video_view)
                        val placeholder = view.findViewById<TextView>(R.id.video_placeholder)

                        firestore.collection("children").document(childID).get()
                            .addOnSuccessListener { doc ->
                                val videoUrl = doc.getString("dreamVideoUrl")
                                if (!videoUrl.isNullOrEmpty()) {
                                    binding.videoPlayer.visibility = View.VISIBLE

                                    val uri = Uri.parse(videoUrl)
                                    videoView.setVideoURI(uri)

                                    videoView.setOnPreparedListener { mp ->
                                        mp.isLooping = false
                                        videoView.start()

                                        // Hide the placeholder when the video is ready
                                        placeholder.visibility = View.GONE

                                        val mediaController = MediaController(requireContext())
                                        mediaController.setAnchorView(videoView)
                                        videoView.setMediaController(mediaController)
                                    }

                                } else {
                                    videoView.visibility = View.GONE
                                    placeholder.visibility = View.GONE
                                }
                            }
                            .addOnFailureListener {
                                placeholder.visibility = View.GONE
                                Toast.makeText(requireContext(), "Failed to load video", Toast.LENGTH_SHORT).show()
                            }


                        firestore.collection("requests")
                            .whereEqualTo("childID", childID)
                            .get()
                            .addOnSuccessListener { result ->
                                trace.stop()
                                val requests =
                                    result.documents.mapNotNull { it.toObject(com.example.myapplication.requestlist.Request::class.java) }
                                        .filter {
                                            it.status in listOf(
                                                "Waiting",
                                                "In process",
                                                "Done"
                                            )
                                        }
                                requestAdapter?.submitList(requests)
                            }
                            .addOnFailureListener {
                                trace.stop()
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to load requests",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load child info", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
